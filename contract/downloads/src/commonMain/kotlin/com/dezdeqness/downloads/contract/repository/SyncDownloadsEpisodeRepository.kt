package com.dezdeqness.downloads.contract.repository

import com.dezdeqness.downloads.contract.model.DownloadStatus

interface SyncDownloadsEpisodeRepository {

    suspend fun updateStatus(id: Long, status: DownloadStatus)

    suspend fun updateProgress(id: Long, progress: Float, downloadedSegments: Int)

    suspend fun updateTotalSegments(id: Long, totalSegments: Int)

    suspend fun updateFilePath(id: Long, filePath: String)

    suspend fun markCompleted(id: Long)

    suspend fun hideFromHistory(id: Long)

    suspend fun delete(id: Long)
}
