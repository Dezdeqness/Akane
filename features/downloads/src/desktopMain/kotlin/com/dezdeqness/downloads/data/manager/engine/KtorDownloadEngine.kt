package com.dezdeqness.downloads.data.manager.engine

import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.contract.repository.SyncDownloadsEpisodeRepository
import com.dezdeqness.downloads.data.manager.DownloadFileManager
import com.dezdeqness.downloads.data.manager.pipeline.EpisodeDownloadPipeline
import com.dezdeqness.downloads.notification.DownloadEvent
import com.dezdeqness.downloads.notification.DownloadEventDispatcher
import com.dezdeqness.downloads.notification.DownloadNotificationInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

class KtorDownloadEngine(
    private val pipeline: EpisodeDownloadPipeline,
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncRepository: SyncDownloadsEpisodeRepository,
    private val fileManager: DownloadFileManager,
    private val eventDispatcher: DownloadEventDispatcher,
    private val coroutineScope: CoroutineScope,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val analytics: AkaneAnalytics,
    private val errorReporter: AkaneErrorReporter,
) : DownloadEngine {

    private val jobsMutex = Mutex()
    private val activeJobs = mutableMapOf<Long, Job>()
    private val pausedIds = mutableSetOf<Long>()
    private val downloadSemaphore = Semaphore(MAX_CONCURRENT_DOWNLOADS)

    override suspend fun recover() {
        val staleStatuses = listOf(
            DownloadStatus.DOWNLOADING,
            DownloadStatus.QUEUED,
            DownloadStatus.PAUSED,
        )
        val staleDownloads = downloadEpisodeRepository.getByStatuses(staleStatuses)

        Logger.d(TAG) { "Recovery: found ${staleDownloads.size} stale downloads" }

        for (download in staleDownloads) {
            when (download.status) {
                DownloadStatus.DOWNLOADING,
                DownloadStatus.QUEUED -> {
                    Logger.d(TAG) { "Recovering stale ${download.status} id=${download.id}, re-enqueuing" }
                    syncRepository.updateStatus(download.id, DownloadStatus.QUEUED)
                    enqueue(download)
                }

                DownloadStatus.PAUSED -> {
                    Logger.d(TAG) { "Recovering PAUSED id=${download.id}, resuming" }
                    enqueue(download)
                }

                else -> Unit
            }
        }
    }

    override fun enqueue(download: DownloadEntity) {
        coroutineScope.launch {
            jobsMutex.withLock {
                activeJobs[download.id]
                    ?.takeIf { it.isActive }
                    ?.let { return@withLock }

                coroutineScope.launch(coroutineDispatcherProvider.io()) {
                    runDownloadJob(download)
                }.also { activeJobs[download.id] = it }
            }
        }
    }

    override suspend fun pause(downloadId: Long) {
        val job = jobsMutex.withLock {
            val activeJob = activeJobs[downloadId] ?: return
            pausedIds.add(downloadId)
            activeJob.cancel()
            activeJob
        }

        joinSafely(job)
    }

    override suspend fun cancel(downloadId: Long) {
        val job = jobsMutex.withLock {
            pausedIds.remove(downloadId)
            val activeJob = activeJobs[downloadId]
            activeJob?.cancel()
            activeJob
        }

        joinSafely(job)
    }

    override suspend fun delete(download: DownloadEntity) {
        cancel(download.id)
        fileManager.cleanupTempSegments(download)
        download.filePath?.let { relativePath ->
            fileManager.deleteOutputFile(fileManager.resolveFilePath(relativePath))
        }
    }

    private suspend fun runDownloadJob(download: DownloadEntity) {
        val downloadId = download.id

        Logger.d(TAG) { "Download queued id=$downloadId" }
        syncRepository.updateStatus(downloadId, DownloadStatus.QUEUED)
        eventDispatcher.emit(DownloadEvent.Queued(DownloadNotificationInfo.from(download)))

        try {
            downloadSemaphore.withPermit {
                Logger.d(TAG) { "Starting download id=$downloadId" }
                pipeline.execute(download)
                Logger.d(TAG) { "Download completed id=$downloadId" }
            }
        } catch (e: CancellationException) {
            withContext(NonCancellable) {
                handleCancellation(downloadId)
            }
            throw e
        } catch (e: Exception) {
            handleFailure(downloadId, e)
        } finally {
            withContext(NonCancellable) {
                jobsMutex.withLock {
                    activeJobs.remove(downloadId)
                }
            }
        }
    }

    private suspend fun handleFailure(downloadId: Long, error: Exception) {
        Logger.e(TAG, error) { "Download failed id=$downloadId: ${error.message}" }

        downloadEpisodeRepository.getById(downloadId)?.let { download ->
            errorReporter.captureException(
                throwable = error,
                message = "Download job failed",
                tags = mapOf("feature" to "downloads"),
                extras = mapOf(
                    "download_id" to download.id.toString(),
                    "release_id" to download.releaseId.toString(),
                    "release_title" to download.releaseTitle,
                    "episode_id" to download.episodeId,
                    "episode_name" to download.episodeName,
                    "quality" to download.quality,
                    "status" to download.status.name,
                ),
            )
            analytics.trackEpisodeDownloadFailed(
                episodeId = download.episodeId,
                animeId = download.releaseId,
                animeTitle = download.releaseTitle,
            )
            eventDispatcher.emit(DownloadEvent.Failed(DownloadNotificationInfo.from(download)))
        }
        syncRepository.updateStatus(downloadId, DownloadStatus.FAILED)
    }

    private suspend fun handleCancellation(downloadId: Long) {
        val download = downloadEpisodeRepository.getById(downloadId)
        val wasPaused = jobsMutex.withLock { pausedIds.remove(downloadId) }

        if (wasPaused) {
            Logger.d(TAG) { "Download paused id=$downloadId" }
            syncRepository.updateStatus(downloadId, DownloadStatus.PAUSED)
            download?.let { eventDispatcher.emit(DownloadEvent.Paused(DownloadNotificationInfo.from(it))) }
            return
        }

        Logger.w(TAG) { "Download cancelled id=$downloadId" }
        syncRepository.updateStatus(downloadId, DownloadStatus.CANCELLED)
        download?.let {
            fileManager.cleanupTempSegments(it)
            eventDispatcher.emit(DownloadEvent.Cancelled(DownloadNotificationInfo.from(it)))
        }
    }

    private suspend fun joinSafely(job: Job?) {
        try {
            job?.join()
        } catch (_: CancellationException) {
        }
    }

    companion object {
        private const val TAG = "KtorDownloadEngine"
        private const val MAX_CONCURRENT_DOWNLOADS = 1
    }
}
