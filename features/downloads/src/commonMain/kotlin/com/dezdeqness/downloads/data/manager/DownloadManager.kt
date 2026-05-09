package com.dezdeqness.downloads.data.manager

import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.downloads.data.hls.HlsParser
import com.dezdeqness.downloads.data.network.HlsDownloadService
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.contract.repository.SyncDownloadsEpisodeRepository
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import okio.Path
import kotlin.time.TimeSource

class DownloadManager(
    private val hlsDownloadService: HlsDownloadService,
    private val hlsParser: HlsParser,
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncRepository: SyncDownloadsEpisodeRepository,
    private val fileManager: DownloadFileManager,
    private val coroutineScope: CoroutineScope,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val analytics: AkaneAnalytics,
    private val errorReporter: AkaneErrorReporter,
    private val remuxEnabled: Boolean = false,
) {
    private val jobsMutex = Mutex()
    private val activeJobs = mutableMapOf<Long, Job>()
    private val pausedIds = mutableSetOf<Long>()
    private val downloadSemaphore = Semaphore(MAX_CONCURRENT_DOWNLOADS)

    suspend fun recoverStaleDownloads() {
        val staleStatuses = listOf(
            DownloadStatus.REMUXING,
            DownloadStatus.DOWNLOADING,
            DownloadStatus.QUEUED,
            DownloadStatus.PAUSED,
        )
        val staleDownloads = downloadEpisodeRepository.getByStatuses(staleStatuses)

        Logger.d(TAG) { "Recovery: found ${staleDownloads.size} stale downloads" }

        for (download in staleDownloads) {
            when (download.status) {
                DownloadStatus.REMUXING -> {
                    Logger.d(TAG) { "Recovering stale REMUXING id=${download.id}, restarting remux" }
                    coroutineScope.launch(coroutineDispatcherProvider.io()) {
                        recoverRemux(download)
                    }
                }

                DownloadStatus.DOWNLOADING,
                DownloadStatus.QUEUED -> {
                    Logger.d(TAG) { "Recovering stale ${download.status} id=${download.id}, re-enqueuing" }
                    syncRepository.updateStatus(download.id, DownloadStatus.QUEUED)
                    enqueue(download.id)
                }

                DownloadStatus.PAUSED -> {
                    Logger.d(TAG) { "Recovering PAUSED id=${download.id}, resuming" }
                    enqueue(download.id)
                }

                else -> Unit
            }
        }
    }

    private suspend fun recoverRemux(download: DownloadEntity) {
        try {
            val outputPath = fileManager.getOutputPath(download)
            val mp4Path = fileManager.getMp4Path(outputPath)

            if (fileManager.fileExists(mp4Path)) {
                Logger.d(TAG) { "[${download.id}] .mp4 already exists at $mp4Path, marking completed" }
                fileManager.deleteIfExists(outputPath)
                syncRepository.updateFilePath(download.id, fileManager.toRelativePath(mp4Path.toString()))
                syncRepository.markCompleted(download.id)
                return
            }

            if (fileManager.fileExists(outputPath)) {
                if (remuxEnabled) {
                    Logger.d(TAG) { "[${download.id}] .ts file found, attempting remux" }
                    syncRepository.updateStatus(download.id, DownloadStatus.REMUXING)
                    val remuxResult = fileManager.remux(outputPath)
                    syncRepository.updateFilePath(download.id, fileManager.toRelativePath(remuxResult.filePath))
                    syncRepository.markCompleted(download.id)
                } else {
                    Logger.d(TAG) { "[${download.id}] .ts file found, marking completed" }
                    syncRepository.updateFilePath(download.id, fileManager.toRelativePath(outputPath.toString()))
                    syncRepository.markCompleted(download.id)
                }
                return
            }

            Logger.w(TAG) { "[${download.id}] No .ts found, re-enqueuing" }
            syncRepository.updateStatus(download.id, DownloadStatus.QUEUED)
            enqueue(download.id)
        } catch (e: Exception) {
            Logger.e(TAG, e) { "[${download.id}] Recovery error: ${e.message}" }
            errorReporter.captureException(
                throwable = e,
                message = "Recover remux failed",
                tags = mapOf("feature" to "downloads"),
                extras = download.errorExtras(),
            )
            syncRepository.updateStatus(download.id, DownloadStatus.FAILED)
        }
    }

    fun enqueue(downloadId: Long) {
        coroutineScope.launch {
            val job = jobsMutex.withLock {
                activeJobs[downloadId]
                    ?.takeIf { it.isActive }
                    ?.let { return@withLock null }

                coroutineScope.launch(coroutineDispatcherProvider.io()) {
                    runDownloadJob(downloadId)
                }.also { activeJobs[downloadId] = it }
            }

            job ?: return@launch
        }
    }

    suspend fun pause(downloadId: Long) {
        val job = jobsMutex.withLock {
            val activeJob = activeJobs[downloadId] ?: return
            pausedIds.add(downloadId)
            activeJob.cancel()
            activeJob
        }

        joinSafely(job)
    }

    suspend fun cancel(downloadId: Long) {
        val job = jobsMutex.withLock {
            pausedIds.remove(downloadId)
            val activeJob = activeJobs[downloadId]
            activeJob?.cancel()
            activeJob
        }

        joinSafely(job)
    }

    private suspend fun runDownloadJob(downloadId: Long) {
        Logger.d(TAG) { "Download queued id=$downloadId" }
        syncRepository.updateStatus(downloadId, DownloadStatus.QUEUED)

        try {
            downloadSemaphore.withPermit {
                Logger.d(TAG) { "Starting download id=$downloadId" }
                executeDownload(downloadId)
                Logger.d(TAG) { "Download completed id=$downloadId" }
            }
        } catch (e: CancellationException) {
            handleCancellation(downloadId)
            throw e
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Download failed id=$downloadId: ${e.message}" }
            downloadEpisodeRepository.getById(downloadId)?.let { download ->
                errorReporter.captureException(
                    throwable = e,
                    message = "Download job failed",
                    tags = mapOf("feature" to "downloads"),
                    extras = download.errorExtras(),
                )
                analytics.trackEpisodeDownloadFailed(
                    episodeId = download.episodeId,
                    animeId = download.releaseId,
                    animeTitle = download.releaseTitle,
                )
            }
            syncRepository.updateStatus(downloadId, DownloadStatus.FAILED)
        } finally {
            jobsMutex.withLock {
                activeJobs.remove(downloadId)
            }
        }
    }

    private suspend fun executeDownload(downloadId: Long) {
        val download = requireDownload(downloadId) ?: return

        syncRepository.updateStatus(downloadId, DownloadStatus.DOWNLOADING)

        val playlist = fetchAndParsePlaylist(downloadId, download)
        val totalSegments = playlist.segments.size

        ensurePlaylistNotEmpty(downloadId, totalSegments)
        syncRepository.updateTotalSegments(downloadId, totalSegments)

        logResumeState(downloadId, download, totalSegments)

        val outputPath = fileManager.getOutputPath(download)
        val segmentsDir = fileManager.getSegmentsDir(download)

        Logger.d(TAG) { "[$downloadId] Output: $outputPath" }
        Logger.d(TAG) {
            "[$downloadId] Downloading $totalSegments segments in chunks of $SEGMENTS_PER_DOWNLOAD " +
                    "(already cached: ~${download.downloadedSegments})"
        }

        val result = downloadAllSegments(
            downloadId = downloadId,
            segmentUrls = playlist.segments,
            segmentsDir = segmentsDir,
        )

        validateFailedSegments(downloadId, result.failedSegments, result.totalSegments)

        finishDownload(
            download = download,
            outputPath = outputPath,
            segmentsDir = segmentsDir,
            totalSegments = result.totalSegments,
            segmentDurations = playlist.segmentDurations,
            targetDuration = playlist.targetDuration,
        )
    }

    private suspend fun requireDownload(downloadId: Long): DownloadEntity? {
        val download = downloadEpisodeRepository.getById(downloadId)
        if (download == null) {
            Logger.e(TAG) { "Download not found in DB id=$downloadId" }
            errorReporter.captureMessage(
                message = "Download not found in database",
                tags = mapOf("feature" to "downloads"),
            )
        }
        return download
    }

    private suspend fun fetchAndParsePlaylist(
        downloadId: Long,
        download: DownloadEntity,
    ): ParsedPlaylist {
        Logger.d(TAG) { "[$downloadId] Fetching m3u8: ${download.hlsUrl}" }

        val m3u8Content = try {
            hlsDownloadService.fetchM3u8(download.hlsUrl)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(TAG, e) { "[$downloadId] Failed to fetch m3u8: url=${download.hlsUrl}" }
            throw e
        }

        Logger.d(TAG) { "[$downloadId] m3u8 content length=${m3u8Content.length}" }
        Logger.d(TAG) { "[$downloadId] m3u8 raw content:\n${m3u8Content.take(1000)}" }

        val playlist = try {
            hlsParser.parse(m3u8Content, download.hlsUrl)
        } catch (e: Exception) {
            Logger.e(TAG, e) {
                "[$downloadId] Failed to parse m3u8: url=${download.hlsUrl}, content:\n${
                    m3u8Content.take(
                        500
                    )
                }"
            }
            throw e
        }

        Logger.d(TAG) { "[$downloadId] Parsed ${playlist.segments.size} segments" }
        return ParsedPlaylist(
            segments = playlist.segments,
            segmentDurations = playlist.segmentDurations,
            targetDuration = playlist.targetDuration,
            rawContentPreview = m3u8Content.take(500),
        )
    }

    private fun ensurePlaylistNotEmpty(
        downloadId: Long,
        totalSegments: Int,
    ) {
        if (totalSegments == 0) {
            Logger.e(TAG) { "[$downloadId] No segments found in m3u8" }
            throw IllegalStateException("No segments found in m3u8 playlist")
        }
    }

    private fun logResumeState(
        downloadId: Long,
        download: DownloadEntity,
        totalSegments: Int,
    ) {
        Logger.d(TAG) {
            "[$downloadId] Already downloaded: ${download.downloadedSegments}/$totalSegments segments"
        }
    }

    private suspend fun downloadAllSegments(
        downloadId: Long,
        segmentUrls: List<String>,
        segmentsDir: Path,
    ): DownloadSegmentsResult {
        val totalSegments = segmentUrls.size
        val downloadStartTime = TimeSource.Monotonic.markNow()

        var completedCount = 0
        val failedSegments = mutableListOf<Int>()

        for (chunk in (0 until totalSegments).chunked(SEGMENTS_PER_DOWNLOAD)) {
            currentCoroutineContext().ensureActive()

            val chunkResult = downloadChunk(
                downloadId = downloadId,
                chunk = chunk,
                segmentUrls = segmentUrls,
                totalSegments = totalSegments,
                segmentsDir = segmentsDir,
            )

            completedCount += chunkResult.completedCount
            failedSegments += chunkResult.failedSegments

            updateAndLogProgress(
                downloadId = downloadId,
                completedCount = completedCount,
                totalSegments = totalSegments,
                elapsedSeconds = downloadStartTime.elapsedNow().inWholeSeconds,
            )
        }

        Logger.d(TAG) {
            "[$downloadId] All segments finished in ${downloadStartTime.elapsedNow().inWholeSeconds}s " +
                    "(failed: ${failedSegments.size})"
        }

        return DownloadSegmentsResult(
            totalSegments = totalSegments,
            completedCount = completedCount,
            failedSegments = failedSegments,
        )
    }

    private suspend fun downloadChunk(
        downloadId: Long,
        chunk: List<Int>,
        segmentUrls: List<String>,
        totalSegments: Int,
        segmentsDir: Path,
    ): ChunkDownloadResult {
        val chunkResults = supervisorScope {
            chunk.map { index ->
                async(coroutineDispatcherProvider.io()) {
                    downloadSingleSegmentSafely(
                        downloadId = downloadId,
                        segmentIndex = index,
                        totalSegments = totalSegments,
                        segmentUrl = segmentUrls[index],
                        outputPath = fileManager.getSegmentPath(segmentsDir, index),
                    )
                }
            }.awaitAll()
        }

        var completedCount = 0
        val failedSegments = mutableListOf<Int>()

        chunkResults.forEachIndexed { i, success ->
            if (success) {
                completedCount++
            } else {
                failedSegments += chunk[i]
            }
        }

        return ChunkDownloadResult(
            completedCount = completedCount,
            failedSegments = failedSegments,
        )
    }

    private suspend fun downloadSingleSegmentSafely(
        downloadId: Long,
        segmentIndex: Int,
        totalSegments: Int,
        segmentUrl: String,
        outputPath: Path,
    ): Boolean {
        return try {
            downloadSegmentToFile(
                downloadId = downloadId,
                segmentUrl = segmentUrl,
                segmentIndex = segmentIndex,
                totalSegments = totalSegments,
                outputPath = outputPath,
            )
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(TAG) {
                "[$downloadId] Segment ${segmentIndex + 1}/$totalSegments failed after retries: ${e.message}"
            }
            false
        }
    }

    private suspend fun updateAndLogProgress(
        downloadId: Long,
        completedCount: Int,
        totalSegments: Int,
        elapsedSeconds: Long,
    ) {
        val progress = completedCount.toFloat() / totalSegments
        syncRepository.updateProgress(downloadId, progress, completedCount)

        Logger.d(TAG) {
            "[$downloadId] Progress: $completedCount/$totalSegments segments " +
                    "(${(progress * 100).toInt()}%) in ${elapsedSeconds}s"
        }
    }

    private fun validateFailedSegments(
        downloadId: Long,
        failedSegments: List<Int>,
        totalSegments: Int,
    ) {
        if (failedSegments.isEmpty()) return

        throw IllegalStateException(
            "[$downloadId] Failed to download ${failedSegments.size} segments " +
                    "of $totalSegments: ${failedSegments.sorted().map { it + 1 }}"
        )
    }

    private suspend fun finishDownload(
        download: DownloadEntity,
        outputPath: Path,
        segmentsDir: Path,
        totalSegments: Int,
        segmentDurations: List<Double> = emptyList(),
        targetDuration: Int = 12,
    ) {
        val downloadId = download.id

        if (remuxEnabled) {
            Logger.d(TAG) { "[$downloadId] Merging $totalSegments segments into $outputPath" }
            val totalBytesWritten = fileManager.mergeSegments(segmentsDir, totalSegments, outputPath)
            fileManager.cleanupSegmentsDir(segmentsDir, totalSegments)

            Logger.d(TAG) {
                "[$downloadId] Download complete: $totalSegments segments, total size: " +
                        DownloadFileManager.formatSize(totalBytesWritten)
            }

            syncRepository.updateStatus(downloadId, DownloadStatus.REMUXING)
            Logger.d(TAG) { "[$downloadId] Remuxing..." }

            val remuxResult = fileManager.remux(outputPath)
            syncRepository.updateFilePath(downloadId, fileManager.toRelativePath(remuxResult.filePath))
            syncRepository.markCompleted(downloadId)
            analytics.trackEpisodeDownloadSucceeded(
                episodeId = download.episodeId,
                animeId = download.releaseId,
                animeTitle = download.releaseTitle,
            )

            if (remuxResult.success) {
                Logger.d(TAG) { "[$downloadId] Remux successful: ${remuxResult.filePath}" }
            } else {
                Logger.w(TAG) { "[$downloadId] Remux failed, keeping .ts: ${remuxResult.filePath}" }
            }
        } else {
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
    }

    private suspend fun downloadSegmentToFile(
        downloadId: Long,
        segmentUrl: String,
        segmentIndex: Int,
        totalSegments: Int,
        outputPath: Path,
    ) {
        if (fileManager.segmentExists(outputPath)) {
            Logger.d(TAG) { "[$downloadId] Segment ${segmentIndex + 1}/$totalSegments already exists, skipping" }
            return
        }

        var lastException: Exception? = null

        for (attempt in 1..MAX_RETRIES) {
            currentCoroutineContext().ensureActive()

            try {
                downloadSegmentAttempt(segmentUrl, outputPath)
                logSegmentDownloaded(downloadId, segmentIndex, totalSegments, outputPath)
                return
            } catch (e: CancellationException) {
                fileManager.deleteFile(outputPath)
                throw e
            } catch (e: Exception) {
                fileManager.deleteFile(outputPath)
                lastException = e
                handleSegmentRetryLog(
                    downloadId = downloadId,
                    segmentIndex = segmentIndex,
                    totalSegments = totalSegments,
                    attempt = attempt,
                    error = e,
                )
            }
        }

        Logger.e(TAG, lastException) {
            "[$downloadId] Failed segment ${segmentIndex + 1}/$totalSegments " +
                    "after $MAX_RETRIES attempts: $segmentUrl"
        }
        throw checkNotNull(lastException)
    }

    private suspend fun downloadSegmentAttempt(
        segmentUrl: String,
        outputPath: Path,
    ) {
        val sink = fileManager.writeSink(outputPath)
        try {
            hlsDownloadService.downloadSegment(segmentUrl).execute { response ->
                val channel = response.bodyAsChannel()
                val buffer = ByteArray(BUFFER_SIZE)

                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer)
                    if (read > 0) {
                        sink.write(buffer, 0, read)
                    }
                }
            }
        } finally {
            sink.close()
        }
    }

    private fun logSegmentDownloaded(
        downloadId: Long,
        segmentIndex: Int,
        totalSegments: Int,
        outputPath: Path,
    ) {
        Logger.d(TAG) {
            val size = fileManager.segmentSize(outputPath)
            "[$downloadId] Segment ${segmentIndex + 1}/$totalSegments: ${
                DownloadFileManager.formatSize(
                    size
                )
            }"
        }
    }

    private suspend fun handleSegmentRetryLog(
        downloadId: Long,
        segmentIndex: Int,
        totalSegments: Int,
        attempt: Int,
        error: Exception,
    ) {
        if (attempt >= MAX_RETRIES) return

        val delayMs = attempt * 1_000L
        Logger.w(TAG) {
            "[$downloadId] Segment ${segmentIndex + 1}/$totalSegments failed " +
                    "(attempt $attempt/$MAX_RETRIES), retrying in ${delayMs}ms: ${error.message}"
        }
        delay(delayMs)
    }

    private suspend fun handleCancellation(downloadId: Long) {
        val download = downloadEpisodeRepository.getById(downloadId)
        val wasPaused = jobsMutex.withLock { pausedIds.remove(downloadId) }

        if (wasPaused) {
            Logger.d(TAG) { "Download paused id=$downloadId" }
            syncRepository.updateStatus(downloadId, DownloadStatus.PAUSED)
            return
        }

        Logger.w(TAG) { "Download cancelled id=$downloadId" }
        syncRepository.updateStatus(downloadId, DownloadStatus.CANCELLED)
        download?.let { fileManager.cleanupTempSegments(it) }
    }

    private suspend fun joinSafely(job: Job?) {
        try {
            job?.join()
        } catch (_: CancellationException) {
        }
    }

    private data class ParsedPlaylist(
        val segments: List<String>,
        val segmentDurations: List<Double>,
        val targetDuration: Int,
        val rawContentPreview: String,
    )

    private data class ChunkDownloadResult(
        val completedCount: Int,
        val failedSegments: List<Int>,
    )

    private data class DownloadSegmentsResult(
        val totalSegments: Int,
        val completedCount: Int,
        val failedSegments: List<Int>,
    )

    companion object {
        private const val TAG = "DownloadManager"
        private const val MAX_CONCURRENT_DOWNLOADS = 1
        private const val SEGMENTS_PER_DOWNLOAD = 6
        private const val MAX_RETRIES = 5
        private const val BUFFER_SIZE = 8192
    }
}

private fun DownloadEntity.errorExtras(): Map<String, String> = mapOf(
    "download_id" to id.toString(),
    "release_id" to releaseId.toString(),
    "release_title" to releaseTitle,
    "episode_id" to episodeId,
    "episode_name" to episodeName,
    "quality" to quality,
    "status" to status.name,
)
