package com.dezdeqness.downloads.data.manager.engine

import android.content.Context
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import co.touchlab.kermit.Logger
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.contract.repository.SyncDownloadsEpisodeRepository
import com.dezdeqness.downloads.data.manager.DownloadFileManager
import com.dezdeqness.downloads.notification.DownloadEvent
import com.dezdeqness.downloads.notification.DownloadEventDispatcher
import com.dezdeqness.downloads.notification.DownloadNotificationInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.media3.exoplayer.offline.DownloadManager as Media3DownloadManager
import androidx.core.net.toUri

@UnstableApi
class Media3DownloadEngine(
    private val context: Context,
    private val media3Manager: Media3DownloadManager,
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncRepository: SyncDownloadsEpisodeRepository,
    private val fileManager: DownloadFileManager,
    private val eventDispatcher: DownloadEventDispatcher,
    private val coroutineScope: CoroutineScope,
) : DownloadEngine {

    private var progressJob: Job? = null

    init {
        media3Manager.maxParallelDownloads = MAX_CONCURRENT_DOWNLOADS
        media3Manager.addListener(object : Media3DownloadManager.Listener {
            override fun onDownloadChanged(
                downloadManager: Media3DownloadManager,
                download: Download,
                finalException: Exception?,
            ) {
                Logger.d(TAG) {
                    "state ${download.request.id} → ${stateName(download.state)}" +
                            " stopReason=${download.stopReason}" +
                            (finalException?.let { " error=${it.message}" } ?: "")
                }
                handleDownloadChanged(download, finalException)
            }

            override fun onDownloadRemoved(
                downloadManager: Media3DownloadManager,
                download: Download,
            ) {
                Logger.d(TAG) { "removed ${download.request.id}" }
            }
        })
    }

    override suspend fun recover() {
        cleanupLegacyDownloads()

        val staleStatuses = listOf(
            DownloadStatus.DOWNLOADING,
            DownloadStatus.QUEUED,
        )
        val staleDownloads = downloadEpisodeRepository.getByStatuses(staleStatuses)
        Logger.d(TAG) { "Recovery: re-enqueuing ${staleDownloads.size} stale downloads" }
        staleDownloads.forEach(::enqueue)
    }

    override fun enqueue(download: DownloadEntity) {
        coroutineScope.launch {
            syncRepository.updateStatus(download.id, DownloadStatus.QUEUED)
            eventDispatcher.emit(DownloadEvent.Queued(DownloadNotificationInfo.from(download)))
        }

        val request = DownloadRequest.Builder(contentId(download.id), download.hlsUrl.toUri())
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setData(notificationData(download))
            .build()

        sendCommandSafely("addDownload") { foreground ->
            DownloadService.sendAddDownload(context, AkaneDownloadService::class.java, request, foreground)
            DownloadService.sendSetStopReason(
                context,
                AkaneDownloadService::class.java,
                contentId(download.id),
                Download.STOP_REASON_NONE,
                foreground,
            )
        }
    }

    override suspend fun pause(downloadId: Long) {
        sendCommandSafely("setStopReason") { foreground ->
            DownloadService.sendSetStopReason(
                context,
                AkaneDownloadService::class.java,
                contentId(downloadId),
                STOP_REASON_PAUSED,
                foreground,
            )
        }
        syncRepository.updateStatus(downloadId, DownloadStatus.PAUSED)
        downloadEpisodeRepository.getById(downloadId)?.let {
            eventDispatcher.emit(DownloadEvent.Paused(DownloadNotificationInfo.from(it)))
        }
    }

    override suspend fun cancel(downloadId: Long) {
        sendCommandSafely("removeDownload") { foreground ->
            DownloadService.sendRemoveDownload(
                context,
                AkaneDownloadService::class.java,
                contentId(downloadId),
                foreground,
            )
        }
        syncRepository.updateStatus(downloadId, DownloadStatus.CANCELLED)
        downloadEpisodeRepository.getById(downloadId)?.let {
            eventDispatcher.emit(DownloadEvent.Cancelled(DownloadNotificationInfo.from(it)))
        }
    }

    override suspend fun delete(download: DownloadEntity) {
        sendCommandSafely("removeDownload") { foreground ->
            DownloadService.sendRemoveDownload(
                context,
                AkaneDownloadService::class.java,
                contentId(download.id),
                foreground,
            )
        }
    }

    private fun handleDownloadChanged(download: Download, finalException: Exception?) {
        val downloadId = downloadIdOf(download.request.id) ?: return

        coroutineScope.launch {
            when (download.state) {
                Download.STATE_DOWNLOADING -> {
                    syncRepository.updateStatus(downloadId, DownloadStatus.DOWNLOADING)
                    emitWithInfo(downloadId) { DownloadEvent.Started(it, download.percentDownloaded / 100f) }
                    startProgressPolling()
                }

                Download.STATE_COMPLETED -> {
                    val entity = downloadEpisodeRepository.getById(downloadId) ?: return@launch
                    syncRepository.updateProgress(downloadId, 1f, 0)
                    syncRepository.updateFilePath(downloadId, entity.hlsUrl)
                    syncRepository.markCompleted(downloadId)
                    eventDispatcher.emit(DownloadEvent.Completed(DownloadNotificationInfo.from(entity)))
                }

                Download.STATE_FAILED -> {
                    Logger.e(TAG, finalException) { "Download failed id=$downloadId" }
                    syncRepository.updateStatus(downloadId, DownloadStatus.FAILED)
                    emitWithInfo(downloadId) { DownloadEvent.Failed(it) }
                }

                Download.STATE_STOPPED -> {
                    if (download.stopReason == STOP_REASON_PAUSED) {
                        syncRepository.updateStatus(downloadId, DownloadStatus.PAUSED)
                    }
                }

                else -> Unit
            }
        }
    }

    private fun startProgressPolling() {
        if (progressJob?.isActive == true) return

        progressJob = coroutineScope.launch {
            val lastPolledBytes = mutableMapOf<Long, Long>()

            while (isActive) {
                val active = media3Manager.currentDownloads
                    .filter { it.state == Download.STATE_DOWNLOADING }
                if (active.isEmpty()) break

                for (download in active) {
                    val downloadId = downloadIdOf(download.request.id) ?: continue

                    val bytes = download.bytesDownloaded
                    val bytesPerSec = lastPolledBytes[downloadId]
                        ?.let { (bytes - it) * 1000 / PROGRESS_POLL_INTERVAL_MS }
                    lastPolledBytes[downloadId] = bytes

                    val percent = download.percentDownloaded
                    Logger.d(TAG) {
                        "[$downloadId] ${if (percent >= 0) "${percent.toInt()}%" else "?%"} " +
                                DownloadFileManager.formatSize(bytes) +
                                (bytesPerSec?.let { " @ ${DownloadFileManager.formatSize(it)}/s" } ?: "")
                    }

                    if (percent < 0) continue

                    val progress = percent / 100f
                    syncRepository.updateProgress(downloadId, progress, 0)
                    emitWithInfo(downloadId) {
                        DownloadEvent.Progress(
                            info = it,
                            progress = progress,
                            downloadedSegments = 0,
                            totalSegments = 0,
                        )
                    }
                }
                delay(PROGRESS_POLL_INTERVAL_MS)
            }
        }
    }

    private fun stateName(state: Int): String = when (state) {
        Download.STATE_QUEUED -> "QUEUED"
        Download.STATE_STOPPED -> "STOPPED"
        Download.STATE_DOWNLOADING -> "DOWNLOADING"
        Download.STATE_COMPLETED -> "COMPLETED"
        Download.STATE_FAILED -> "FAILED"
        Download.STATE_REMOVING -> "REMOVING"
        Download.STATE_RESTARTING -> "RESTARTING"
        else -> "UNKNOWN($state)"
    }

    private suspend fun cleanupLegacyDownloads() {
        val completed = downloadEpisodeRepository.getByStatuses(listOf(DownloadStatus.COMPLETED))
        val legacy = completed.filter { it.filePath?.endsWith(LEGACY_PLAYLIST_NAME) == true }
        if (legacy.isEmpty()) return

        Logger.d(TAG) { "Removing ${legacy.size} legacy downloads" }
        for (download in legacy) {
            download.filePath?.let(fileManager::deleteOutputFile)
            syncRepository.delete(download.id)
        }
    }

    private suspend fun emitWithInfo(
        downloadId: Long,
        event: (DownloadNotificationInfo) -> DownloadEvent,
    ) {
        downloadEpisodeRepository.getById(downloadId)?.let {
            eventDispatcher.emit(event(DownloadNotificationInfo.from(it)))
        }
    }

    private fun sendCommandSafely(name: String, block: (foreground: Boolean) -> Unit) {
        try {
            block(true)
        } catch (e: Exception) {
            // Foreground start not allowed from background (API 31+)
            Logger.w(TAG) { "$name with foreground failed (${e.message}), retrying in background" }
            runCatching { block(false) }
                .onFailure { Logger.e(TAG, it) { "$name failed" } }
        }
    }

    private fun notificationData(download: DownloadEntity): ByteArray =
        "${download.releaseTitle}\nСерия ${download.episodeOrdinal}".encodeToByteArray()

    private fun contentId(downloadId: Long): String = "$CONTENT_ID_PREFIX$downloadId"

    private fun downloadIdOf(contentId: String): Long? =
        contentId.removePrefix(CONTENT_ID_PREFIX).toLongOrNull()

    companion object {
        private const val TAG = "Media3DownloadEngine"
        private const val CONTENT_ID_PREFIX = "dl_"
        private const val MAX_CONCURRENT_DOWNLOADS = 1
        private const val PROGRESS_POLL_INTERVAL_MS = 500L
        private const val LEGACY_PLAYLIST_NAME = "playlist.m3u8"

        const val STOP_REASON_PAUSED = 1
    }
}
