package com.dezdeqness.downloads.data.manager.engine

import co.touchlab.kermit.Logger
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.contract.repository.SyncDownloadsEpisodeRepository
import com.dezdeqness.downloads.data.manager.DownloadFileManager
import com.dezdeqness.downloads.notification.DownloadEvent
import com.dezdeqness.downloads.notification.DownloadEventDispatcher
import com.dezdeqness.downloads.notification.DownloadNotificationInfo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAssetDownloadDelegateProtocol
import platform.AVFoundation.AVAssetDownloadTask
import platform.AVFoundation.AVAssetDownloadURLSession
import platform.AVFoundation.AVURLAsset
import platform.CoreMedia.CMTimeRange
import platform.Foundation.NSError
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionTask
import platform.Foundation.NSURLSessionTaskStateRunning
import platform.Foundation.NSURLSessionTaskStateSuspended
import platform.Foundation.NSValue
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
class AvAssetDownloadEngine(
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncRepository: SyncDownloadsEpisodeRepository,
    private val fileManager: DownloadFileManager,
    private val eventBus: DownloadEventDispatcher,
    private val coroutineScope: CoroutineScope,
) : DownloadEngine {

    private val activeTasks = mutableMapOf<Long, AVAssetDownloadTask>()
    private val suspendedTasks = mutableMapOf<Long, AVAssetDownloadTask>()
    private val pendingIds = ArrayDeque<Long>()
    private val pendingEntities = mutableMapOf<Long, DownloadEntity>()
    private val pendingLocations = mutableMapOf<Long, NSURL>()

    private val session: AVAssetDownloadURLSession by lazy {
        val configuration = NSURLSessionConfiguration
            .backgroundSessionConfigurationWithIdentifier(AvAssetBackgroundSessionHolder.SESSION_IDENTIFIER)
        configuration.sessionSendsLaunchEvents = true

        AVAssetDownloadURLSession.sessionWithConfiguration(
            configuration = configuration,
            assetDownloadDelegate = SessionDelegate(),
            delegateQueue = NSOperationQueue.mainQueue,
        )
    }

    fun warmUp() {
        Logger.e(TAG) { session.toString() }
    }

    override suspend fun recover() {
        println("AkaneDownloads: recover() start")
        cleanupLegacyDownloads()
        println("AkaneDownloads: legacy cleanup done")
        verifyCompletedDownloads()
        println("AkaneDownloads: verify completed done")

        val reattached = reattachDaemonTasks()
        println("AkaneDownloads: reattached=$reattached")

        val staleStatuses = listOf(
            DownloadStatus.DOWNLOADING,
            DownloadStatus.QUEUED,
        )
        val staleDownloads = downloadEpisodeRepository.getByStatuses(staleStatuses)
            .filter { it.id !in reattached }

        println("AkaneDownloads: re-enqueuing ${staleDownloads.map { it.id }}")
        staleDownloads.forEach(::enqueue)
    }

    override fun enqueue(download: DownloadEntity) {
        println("AkaneDownloads: enqueue id=${download.id}")
        onMain {
            val downloadId = download.id
            when {
                // Already downloading: do NOT re-write QUEUED over it, otherwise an
                // active task masquerades as queued until the first progress tick.
                downloadId in activeTasks -> Unit

                downloadId in suspendedTasks -> {
                    if (activeTasks.size < MAX_ACTIVE_DOWNLOADS) {
                        val task = suspendedTasks.remove(downloadId) ?: return@onMain
                        activeTasks[downloadId] = task
                        task.resume()
                        markDownloading(downloadId)
                    } else if (downloadId !in pendingIds) {
                        pendingIds.addLast(downloadId)
                        markQueued(download)
                    }
                }

                downloadId in pendingIds -> Unit

                activeTasks.size < MAX_ACTIVE_DOWNLOADS -> createAndStartTask(download)

                else -> {
                    pendingEntities[downloadId] = download
                    pendingIds.addLast(downloadId)
                    markQueued(download)
                }
            }
        }
    }

    override suspend fun pause(downloadId: Long) {
        onMain {
            pendingIds.remove(downloadId)
            pendingEntities.remove(downloadId)
            activeTasks.remove(downloadId)?.let { task ->
                task.suspend()
                suspendedTasks[downloadId] = task
                startNextPending()
            }
        }
        syncRepository.updateStatus(downloadId, DownloadStatus.PAUSED)
        emitWithInfo(downloadId) { DownloadEvent.Paused(it) }
    }

    override suspend fun cancel(downloadId: Long) {
        // Status goes first: the -999 completion callback checks it to tell a user cancel
        // from a system one, so it must already be CANCELLED when the task is cancelled.
        syncRepository.updateStatus(downloadId, DownloadStatus.CANCELLED)
        onMain {
            removeTask(downloadId)?.cancel()
            pendingLocations.remove(downloadId)?.let(BookmarkPaths::deleteAt)
            startNextPending()
        }
        emitWithInfo(downloadId) { DownloadEvent.Cancelled(it) }
    }

    override suspend fun delete(download: DownloadEntity) {
        // Same reason as in cancel(): the -999 callback must not see QUEUED/DOWNLOADING
        // and re-enqueue a download that is being deleted. Callers drop the row right after.
        if (download.status == DownloadStatus.QUEUED || download.status == DownloadStatus.DOWNLOADING) {
            syncRepository.updateStatus(download.id, DownloadStatus.CANCELLED)
        }
        onMain {
            removeTask(download.id)?.cancel()
            pendingLocations.remove(download.id)?.let(BookmarkPaths::deleteAt)
            startNextPending()
        }
        download.filePath
            ?.let(BookmarkPaths::resolve)
            ?.let(BookmarkPaths::deleteAt)
    }

    private fun createAndStartTask(download: DownloadEntity) {
        val url = NSURL.URLWithString(download.hlsUrl)
        if (url == null) {
            coroutineScope.launch { markFailed(download.id) }
            startNextPending()
            return
        }

        val task = session.assetDownloadTaskWithURLAsset(
            URLAsset = AVURLAsset(uRL = url, options = null),
            assetTitle = download.releaseTitle,
            assetArtworkData = null,
            options = null,
        )
        if (task == null) {
            Logger.e(TAG) { "Failed to create download task id=${download.id}" }
            coroutineScope.launch { markFailed(download.id) }
            startNextPending()
            return
        }

        task.taskDescription = download.id.toString()
        activeTasks[download.id] = task
        task.resume()
        markDownloading(download.id)
        println("AkaneDownloads: started task id=${download.id} state=${task.state} url=${download.hlsUrl.take(80)}")
    }

    private fun startNextPending() {
        while (activeTasks.size < MAX_ACTIVE_DOWNLOADS && pendingIds.isNotEmpty()) {
            val nextId = pendingIds.removeFirst()

            val suspendedTask = suspendedTasks.remove(nextId)
            if (suspendedTask != null) {
                activeTasks[nextId] = suspendedTask
                suspendedTask.resume()
                markDownloading(nextId)
                continue
            }

            val entity = pendingEntities.remove(nextId) ?: continue
            createAndStartTask(entity)
        }
    }

    private fun removeTask(downloadId: Long): AVAssetDownloadTask? {
        pendingIds.remove(downloadId)
        pendingEntities.remove(downloadId)
        return activeTasks.remove(downloadId) ?: suspendedTasks.remove(downloadId)
    }

    private val progressLogged = mutableSetOf<Long>()

    private fun handleProgress(task: AVAssetDownloadTask, progress: Float) {
        val downloadId = downloadIdOf(task) ?: return
        if (progressLogged.add(downloadId)) {
            println("AkaneDownloads: first progress id=$downloadId progress=$progress")
        }
        coroutineScope.launch {
            syncRepository.updateStatus(downloadId, DownloadStatus.DOWNLOADING)
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
    }

    private fun handleFinishedLocation(task: AVAssetDownloadTask, location: NSURL) {
        val downloadId = downloadIdOf(task) ?: return
        pendingLocations[downloadId] = location
    }

    private fun handleCompletion(task: NSURLSessionTask, error: NSError?) {
        println(
            "AkaneDownloads: completion task=${task.taskDescription} " +
                    (error?.let { "error=${it.code} ${it.localizedDescription}" } ?: "success")
        )
        val downloadId = task.taskDescription?.toLongOrNull() ?: return

        activeTasks.remove(downloadId)
        suspendedTasks.remove(downloadId)
        val location = pendingLocations.remove(downloadId)
        startNextPending()

        coroutineScope.launch {
            when {
                error == null && location != null -> {
                    val bookmark = BookmarkPaths.create(location)
                    if (bookmark == null) {
                        Logger.e(TAG) { "Failed to create bookmark for id=$downloadId" }
                        BookmarkPaths.deleteAt(location)
                        markFailed(downloadId)
                        return@launch
                    }
                    syncRepository.updateProgress(downloadId, 1f, 0)
                    syncRepository.updateFilePath(downloadId, bookmark)
                    syncRepository.markCompleted(downloadId)
                    emitWithInfo(downloadId) { DownloadEvent.Completed(it) }
                }

                error != null && error.code == CANCELLED_ERROR_CODE -> {
                    location?.let(BookmarkPaths::deleteAt)
                    val entity = downloadEpisodeRepository.getById(downloadId)
                    if (entity != null &&
                        (entity.status == DownloadStatus.QUEUED || entity.status == DownloadStatus.DOWNLOADING)
                    ) {
                        enqueue(entity)
                    }
                }

                else -> {
                    Logger.e(TAG) { "Download failed id=$downloadId: ${error?.localizedDescription}" }
                    location?.let(BookmarkPaths::deleteAt)
                    markFailed(downloadId)
                }
            }
        }
    }

    private suspend fun reattachDaemonTasks(): Set<Long> {
        // Canceling/completed tasks (e.g. cancelled by iOS on app kill) must not count as
        // reattached — otherwise recover() skips them and they hang in QUEUED forever.
        println("AkaneDownloads: requesting session tasks")
        val allTasks = allSessionTasks()
        println(
            "AkaneDownloads: session tasks: " +
                    allTasks.joinToString { "${it.taskDescription}(state=${it.state})" }.ifEmpty { "none" }
        )
        val tasks = allTasks.filter {
            it.state == NSURLSessionTaskStateRunning || it.state == NSURLSessionTaskStateSuspended
        }
        val reattached = mutableSetOf<Long>()

        for (task in tasks) {
            val assetTask = task as? AVAssetDownloadTask ?: continue
            val downloadId = downloadIdOf(assetTask) ?: continue
            reattached += downloadId
        }

        onMain {
            for (task in tasks) {
                val assetTask = task as? AVAssetDownloadTask ?: continue
                val downloadId = downloadIdOf(assetTask) ?: continue

                if (activeTasks.size < MAX_ACTIVE_DOWNLOADS) {
                    activeTasks[downloadId] = assetTask
                    assetTask.resume()
                    markDownloading(downloadId)
                } else {
                    assetTask.suspend()
                    suspendedTasks[downloadId] = assetTask
                    pendingIds.addLast(downloadId)
                }
            }
        }
        return reattached
    }

    private suspend fun allSessionTasks(): List<NSURLSessionTask> =
        suspendCancellableCoroutine { continuation ->
            session.getAllTasksWithCompletionHandler { tasks ->
                continuation.resume(tasks?.filterIsInstance<NSURLSessionTask>().orEmpty())
            }
        }

    private suspend fun verifyCompletedDownloads() {
        val completed = downloadEpisodeRepository.getByStatuses(listOf(DownloadStatus.COMPLETED))
        for (download in completed) {
            val filePath = download.filePath ?: continue
            if (!filePath.startsWith(BookmarkPaths.PREFIX)) continue

            val resolved = BookmarkPaths.resolve(filePath)
            if (resolved == null || resolved.path == null) {
                Logger.w(TAG) { "Downloaded asset missing (evicted?) id=${download.id}" }
                syncRepository.updateStatus(download.id, DownloadStatus.FAILED)
            }
        }
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

    private fun markDownloading(downloadId: Long) {
        coroutineScope.launch {
            syncRepository.updateStatus(downloadId, DownloadStatus.DOWNLOADING)
        }
    }

    private fun markQueued(download: DownloadEntity) {
        coroutineScope.launch {
            syncRepository.updateStatus(download.id, DownloadStatus.QUEUED)
            eventBus.emit(DownloadEvent.Queued(DownloadNotificationInfo.from(download)))
        }
    }

    private suspend fun markFailed(downloadId: Long) {
        syncRepository.updateStatus(downloadId, DownloadStatus.FAILED)
        emitWithInfo(downloadId) { DownloadEvent.Failed(it) }
    }

    private suspend fun emitWithInfo(
        downloadId: Long,
        event: (DownloadNotificationInfo) -> DownloadEvent,
    ) {
        downloadEpisodeRepository.getById(downloadId)?.let {
            eventBus.emit(event(DownloadNotificationInfo.from(it)))
        }
    }

    private fun downloadIdOf(task: AVAssetDownloadTask): Long? =
        task.taskDescription?.toLongOrNull()

    private fun onMain(block: () -> Unit) {
        dispatch_async(dispatch_get_main_queue(), block)
    }

    private inner class SessionDelegate : NSObject(), AVAssetDownloadDelegateProtocol {

        override fun URLSession(
            session: NSURLSession,
            assetDownloadTask: AVAssetDownloadTask,
            didLoadTimeRange: kotlinx.cinterop.CValue<CMTimeRange>,
            totalTimeRangesLoaded: List<*>,
            timeRangeExpectedToLoad: kotlinx.cinterop.CValue<CMTimeRange>,
        ) {
            val expectedSeconds = timeRangeExpectedToLoad.useContents {
                if (duration.timescale != 0) duration.value.toDouble() / duration.timescale else 0.0
            }
            if (expectedSeconds <= 0.0) return

            var loadedSeconds = 0.0
            totalTimeRangesLoaded.forEach { value ->
                val nsValue = value as? NSValue ?: return@forEach
                loadedSeconds += memScoped {
                    val range = alloc<CMTimeRange>()
                    nsValue.getValue(range.ptr, sizeOf<CMTimeRange>().toULong())
                    if (range.duration.timescale != 0) {
                        range.duration.value.toDouble() / range.duration.timescale
                    } else {
                        0.0
                    }
                }
            }

            val progress = (loadedSeconds / expectedSeconds).toFloat().coerceIn(0f, 1f)
            handleProgress(assetDownloadTask, progress)
        }

        override fun URLSession(
            session: NSURLSession,
            assetDownloadTask: AVAssetDownloadTask,
            didFinishDownloadingToURL: NSURL,
        ) {
            handleFinishedLocation(assetDownloadTask, didFinishDownloadingToURL)
        }

        override fun URLSession(
            session: NSURLSession,
            task: NSURLSessionTask,
            didCompleteWithError: NSError?,
        ) {
            handleCompletion(task, didCompleteWithError)
        }

        override fun URLSessionDidFinishEventsForBackgroundURLSession(session: NSURLSession) {
            onMain {
                AvAssetBackgroundSessionHolder.consumeCompletionHandler()?.invoke()
            }
        }
    }

    companion object {
        private const val TAG = "AvAssetDownloadEngine"
        private const val LEGACY_PLAYLIST_NAME = "playlist.m3u8"
        private const val CANCELLED_ERROR_CODE = -999L
        private const val MAX_ACTIVE_DOWNLOADS = 2
    }
}
