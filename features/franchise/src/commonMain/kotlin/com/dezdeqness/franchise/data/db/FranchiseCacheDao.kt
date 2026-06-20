package com.dezdeqness.franchise.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FranchiseCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FranchiseLocal>)

    @Query("SELECT * FROM franchise_cache WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): FranchiseLocal?

    @Query("DELETE FROM franchise_cache")
    suspend fun clearAll()
}
