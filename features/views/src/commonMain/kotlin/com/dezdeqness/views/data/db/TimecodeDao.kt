package com.dezdeqness.views.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TimecodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: TimecodeLocal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<TimecodeLocal>)

    @Query("SELECT * FROM episode_timecode WHERE releaseId = :releaseId")
    suspend fun getByRelease(releaseId: Long): List<TimecodeLocal>

    @Query("SELECT * FROM episode_timecode WHERE isSynced = 0")
    suspend fun getUnsynced(): List<TimecodeLocal>

    @Query("UPDATE episode_timecode SET isSynced = 1 WHERE releaseEpisodeId IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}
