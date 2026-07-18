package com.dezdeqness.downloads.data.manager.engine

import com.dezdeqness.downloads.contract.model.DownloadEntity

interface DownloadEngine {

    suspend fun recover()

    fun enqueue(download: DownloadEntity)

    suspend fun pause(downloadId: Long)

    suspend fun cancel(downloadId: Long)

    suspend fun delete(download: DownloadEntity)
}
