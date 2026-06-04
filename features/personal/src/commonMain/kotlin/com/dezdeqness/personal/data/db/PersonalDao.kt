package com.dezdeqness.personal.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dezdeqness.personal.data.models.PersonalLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PersonalLocal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PersonalLocal>)

    @Query("DELETE FROM 'personal' WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM 'personal'")
    suspend fun clear()

    @Query("SELECT id FROM 'personal'")
    fun getIdsAsFlow(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM 'personal' WHERE id = :id)")
    suspend fun contains(id: Long): Boolean

    @Transaction
    suspend fun replaceAll(items: List<PersonalLocal>) {
        clear()
        insertAll(items)
    }
}
