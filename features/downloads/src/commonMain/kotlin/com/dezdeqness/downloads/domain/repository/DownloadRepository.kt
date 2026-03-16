package com.dezdeqness.downloads.domain.repository

import com.dezdeqness.downloads.domain.model.DownloadEntity
import com.dezdeqness.downloads.domain.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getAllDownloadsAsFlow(): Flow<List<DownloadEntity>>
    fun getActiveDownloadsCountAsFlow(): Flow<Int>
    suspend fun getById(id: Long): DownloadEntity?
    suspend fun getByEpisodeAndQuality(episodeId: String, quality: String): DownloadEntity?
    suspend fun getByEpisodeId(episodeId: String): List<DownloadEntity>
    suspend fun insert(entity: DownloadEntity): Long
    suspend fun updateStatus(id: Long, status: DownloadStatus)
    suspend fun updateProgress(id: Long, progress: Float, downloadedSegments: Int)
    suspend fun updateTotalSegments(id: Long, totalSegments: Int)
    suspend fun updateFilePath(id: Long, filePath: String)
    suspend fun getCompletedByReleaseId(releaseId: Long): List<DownloadEntity>
    fun getCompletedByReleaseIdAsFlow(releaseId: Long): Flow<List<DownloadEntity>>
    suspend fun delete(id: Long)
    suspend fun deleteFile(id: Long)
}
