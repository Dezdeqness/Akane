package com.dezdeqness.downloads.data.manager

import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.data.manager.engine.DownloadEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DownloadManager(
    private val engine: DownloadEngine,
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val coroutineScope: CoroutineScope,
    private val errorReporter: AkaneErrorReporter,
) {

    suspend fun recoverStaleDownloads() {
        engine.recover()
    }

    fun enqueue(downloadId: Long) {
        coroutineScope.launch {
            val download = requireDownload(downloadId) ?: return@launch
            engine.enqueue(download)
        }
    }

    suspend fun pause(downloadId: Long) {
        engine.pause(downloadId)
    }

    suspend fun cancel(downloadId: Long) {
        engine.cancel(downloadId)
    }

    suspend fun delete(downloadId: Long) {
        val download = downloadEpisodeRepository.getById(downloadId) ?: return
        engine.delete(download)
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

    companion object {
        private const val TAG = "DownloadManager"
    }
}
