package com.dezdeqness.downloads.data.manager.pipeline

import co.touchlab.kermit.Logger
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.downloads.data.manager.DownloadFileManager
import com.dezdeqness.downloads.data.network.HlsDownloadService
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.supervisorScope
import okio.Path
import kotlin.time.TimeSource

class SegmentDownloader(
    private val hlsDownloadService: HlsDownloadService,
    private val fileManager: DownloadFileManager,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    data class Result(
        val totalSegments: Int,
        val completedCount: Int,
        val failedSegments: List<Int>,
    )

    suspend fun downloadAll(
        downloadId: Long,
        segmentUrls: List<String>,
        segmentsDir: Path,
        onProgress: suspend (completedCount: Int, totalSegments: Int) -> Unit,
    ): Result {
        val totalSegments = segmentUrls.size
        val downloadStartTime = TimeSource.Monotonic.markNow()

        var completedCount = 0
        val failedSegments = mutableListOf<Int>()

        for (chunk in (0 until totalSegments).chunked(SEGMENTS_PER_DOWNLOAD)) {
            currentCoroutineContext().ensureActive()

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

            chunkResults.forEachIndexed { i, success ->
                if (success) {
                    completedCount++
                } else {
                    failedSegments += chunk[i]
                }
            }

            onProgress(completedCount, totalSegments)

            Logger.d(TAG) {
                "[$downloadId] Progress: $completedCount/$totalSegments segments " +
                        "in ${downloadStartTime.elapsedNow().inWholeSeconds}s"
            }
        }

        Logger.d(TAG) {
            "[$downloadId] All segments finished in ${downloadStartTime.elapsedNow().inWholeSeconds}s " +
                    "(failed: ${failedSegments.size})"
        }

        return Result(
            totalSegments = totalSegments,
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
                Logger.d(TAG) {
                    val size = fileManager.segmentSize(outputPath)
                    "[$downloadId] Segment ${segmentIndex + 1}/$totalSegments: " +
                            DownloadFileManager.formatSize(size)
                }
                return
            } catch (e: CancellationException) {
                fileManager.deleteFile(outputPath)
                throw e
            } catch (e: Exception) {
                fileManager.deleteFile(outputPath)
                lastException = e
                logRetryAndDelay(
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

    private suspend fun logRetryAndDelay(
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

    companion object {
        private const val TAG = "SegmentDownloader"
        private const val SEGMENTS_PER_DOWNLOAD = 6
        private const val MAX_RETRIES = 5
        private const val BUFFER_SIZE = 8192
    }
}
