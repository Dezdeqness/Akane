package com.dezdeqness.downloads.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SyncDownloadEpisodeDao {

    @Insert
    suspend fun insertProgress(item: DownloadProgressLocal): Long

    @Query("UPDATE download_progress SET status = :status WHERE episodeDownloadId = :episodeDownloadId")
    suspend fun updateStatus(episodeDownloadId: Long, status: String)

    @Query("UPDATE download_progress SET progress = :progress, downloadedSegments = :segments WHERE episodeDownloadId = :episodeDownloadId")
    suspend fun updateProgress(episodeDownloadId: Long, progress: Float, segments: Int)

    @Query("UPDATE download_progress SET totalSegments = :totalSegments WHERE episodeDownloadId = :episodeDownloadId")
    suspend fun updateTotalSegments(episodeDownloadId: Long, totalSegments: Int)

    @Query("UPDATE download_episodes SET filePath = :path WHERE id = :id")
    suspend fun updateFilePath(id: Long, path: String)

    @Query("UPDATE download_progress SET status = 'COMPLETED' WHERE episodeDownloadId = :episodeDownloadId")
    suspend fun markCompleted(episodeDownloadId: Long)

    @Query("UPDATE download_progress SET hiddenFromHistory = 1 WHERE episodeDownloadId = :episodeDownloadId")
    suspend fun hideFromHistory(episodeDownloadId: Long)
}
