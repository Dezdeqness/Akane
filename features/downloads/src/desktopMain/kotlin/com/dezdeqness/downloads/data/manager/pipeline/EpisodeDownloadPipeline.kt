package com.dezdeqness.downloads.data.manager.pipeline

import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.SyncDownloadsEpisodeRepository
import com.dezdeqness.downloads.data.manager.DownloadFileManager
import com.dezdeqness.downloads.notification.DownloadEvent
import com.dezdeqness.downloads.notification.DownloadEventDispatcher
import com.dezdeqness.downloads.notification.DownloadNotificationInfo
import okio.Path

class EpisodeDownloadPipeline(
    private val playlistFetcher: HlsPlaylistFetcher,
    private val segmentDownloader: SegmentDownloader,
    private val fileManager: DownloadFileManager,
    private val syncRepository: SyncDownloadsEpisodeRepository,
    private val eventDispatcher: DownloadEventDispatcher,
    private val analytics: AkaneAnalytics,
) {

    suspend fun execute(download: DownloadEntity) {
        val downloadId = download.id
        val info = DownloadNotificationInfo.from(download)

        syncRepository.updateStatus(downloadId, DownloadStatus.DOWNLOADING)
        eventDispatcher.emit(DownloadEvent.Started(info, download.progress))

        val playlist = playlistFetcher.fetch(downloadId, download.hlsUrl)
        val totalSegments = playlist.segments.size

        syncRepository.updateTotalSegments(downloadId, totalSegments)

        val segmentsDir = fileManager.getSegmentsDir(download)

        Logger.d(TAG) {
            "[$downloadId] Downloading $totalSegments segments " +
                    "(already cached: ~${download.downloadedSegments})"
        }

        val result = segmentDownloader.downloadAll(
            downloadId = downloadId,
            segmentUrls = playlist.segments,
            segmentsDir = segmentsDir,
        ) { completedCount, total ->
            val progress = completedCount.toFloat() / total
            syncRepository.updateProgress(downloadId, progress, completedCount)
            eventDispatcher.emit(
                DownloadEvent.Progress(
                    info = info,
                    progress = progress,
                    downloadedSegments = completedCount,
                    totalSegments = total,
                )
            )
        }

        if (result.failedSegments.isNotEmpty()) {
            throw IllegalStateException(
                "[$downloadId] Failed to download ${result.failedSegments.size} segments " +
                        "of ${result.totalSegments}: ${result.failedSegments.sorted().map { it + 1 }}"
            )
        }

        onDownloadFinish(
            download = download,
            segmentsDir = segmentsDir,
            totalSegments = result.totalSegments,
            segmentDurations = playlist.segmentDurations,
            targetDuration = playlist.targetDuration,
        )

        eventDispatcher.emit(DownloadEvent.Completed(info))
    }

    private suspend fun onDownloadFinish(
        download: DownloadEntity,
        segmentsDir: Path,
        totalSegments: Int,
        segmentDurations: List<Double>,
        targetDuration: Int,
    ) {
        val downloadId = download.id

        val episodeDir = fileManager.getEpisodeDir(download)
        Logger.d(TAG) { "[$downloadId] Moving $totalSegments segments to $episodeDir" }

        fileManager.moveSegmentsToEpisodeDir(segmentsDir, totalSegments, episodeDir)
        fileManager.cleanupSegmentsDir(segmentsDir, totalSegments)

        val playlistPath = fileManager.generateLocalPlaylist(
            episodeDir = episodeDir,
            totalSegments = totalSegments,
            segmentDurations = segmentDurations,
            targetDuration = targetDuration,
        )
        Logger.d(TAG) { "[$downloadId] Generated local playlist: $playlistPath" }

        syncRepository.updateFilePath(downloadId, fileManager.toRelativePath(playlistPath.toString()))
        syncRepository.markCompleted(downloadId)
        analytics.trackEpisodeDownloadSucceeded(
            episodeId = download.episodeId,
            animeId = download.releaseId,
            animeTitle = download.releaseTitle,
        )
    }

    companion object {
        private const val TAG = "EpisodeDownloadPipeline"
    }
}
