package com.dezdeqness.downloads.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadEpisodeDao {

    @Insert
    suspend fun insertEpisode(item: DownloadEpisodeLocal): Long

    @Transaction
    @Query("SELECT * FROM download_episodes ORDER BY createdAt DESC")
    fun getAllWithProgressAsFlow(): Flow<List<DownloadWithProgress>>

    @Transaction
    @Query("SELECT * FROM download_episodes WHERE id = :id")
    suspend fun getWithProgressById(id: Long): DownloadWithProgress?

    @Query("SELECT * FROM download_episodes WHERE episodeId = :episodeId AND quality = :quality LIMIT 1")
    suspend fun getByEpisodeAndQuality(episodeId: String, quality: String): DownloadEpisodeLocal?

    @Query("SELECT * FROM download_episodes WHERE episodeId = :episodeId")
    suspend fun getByEpisodeId(episodeId: String): List<DownloadEpisodeLocal>

    @Transaction
    @Query("SELECT * FROM download_episodes WHERE releaseId = :releaseId AND id IN (SELECT episodeDownloadId FROM download_progress WHERE status = 'COMPLETED') ORDER BY episodeOrdinal ASC")
    suspend fun getCompletedByReleaseId(releaseId: Long): List<DownloadWithProgress>

    @Transaction
    @Query("SELECT * FROM download_episodes WHERE releaseId = :releaseId AND id IN (SELECT episodeDownloadId FROM download_progress WHERE status = 'COMPLETED') ORDER BY episodeOrdinal ASC")
    fun getCompletedByReleaseIdAsFlow(releaseId: Long): Flow<List<DownloadWithProgress>>

    @Transaction
    @Query("SELECT * FROM download_episodes WHERE id IN (SELECT episodeDownloadId FROM download_progress WHERE status IN (:statuses))")
    suspend fun getByStatuses(statuses: List<String>): List<DownloadWithProgress>

    @Query("DELETE FROM download_episodes WHERE id = :id")
    suspend fun delete(id: Long)
}
