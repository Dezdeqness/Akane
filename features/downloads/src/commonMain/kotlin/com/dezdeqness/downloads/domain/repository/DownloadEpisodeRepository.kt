package com.dezdeqness.downloads.domain.repository

import com.dezdeqness.downloads.domain.model.DownloadEntity
import com.dezdeqness.downloads.domain.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

interface DownloadEpisodeRepository {
    fun getAllDownloadsAsFlow(): Flow<List<DownloadEntity>>
    fun getActiveDownloadsCountAsFlow(): Flow<Int>
    suspend fun getById(id: Long): DownloadEntity?
    suspend fun getByEpisodeAndQuality(episodeId: String, quality: String): DownloadEntity?
    suspend fun getByEpisodeId(episodeId: String): List<DownloadEntity>
    suspend fun insert(entity: DownloadEntity): Long
    suspend fun getCompletedByReleaseId(releaseId: Long): List<DownloadEntity>
    fun getCompletedByReleaseIdAsFlow(releaseId: Long): Flow<List<DownloadEntity>>
    suspend fun getByStatuses(statuses: List<DownloadStatus>): List<DownloadEntity>
    suspend fun deleteRecord(id: Long)
}
