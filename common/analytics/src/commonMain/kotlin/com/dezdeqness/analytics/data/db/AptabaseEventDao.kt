package com.dezdeqness.analytics.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AptabaseEventDao {
    @Insert
    suspend fun insert(item: AptabaseEventLocal)

    @Query("SELECT * FROM aptabase_events ORDER BY id ASC LIMIT :limit")
    suspend fun getBatch(limit: Int): List<AptabaseEventLocal>

    @Query("DELETE FROM aptabase_events WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM aptabase_events")
    suspend fun count(): Int
}
