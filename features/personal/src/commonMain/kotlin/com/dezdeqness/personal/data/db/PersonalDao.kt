package com.dezdeqness.personal.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dezdeqness.personal.data.models.PersonalLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PersonalLocal)

    @Query("DELETE from 'personal' WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM 'personal' ORDER BY createdTimeStamp DESC")
    fun getPersonalAsFlow(): Flow<List<PersonalLocal>>

    @Query("SELECT * FROM 'personal' ORDER BY createdTimeStamp DESC")
    suspend fun getPersonalList(): List<PersonalLocal>

    @Query("SELECT EXISTS(SELECT 1 FROM 'personal' WHERE id = :id)")
    suspend fun contains(id: Long): Boolean
}
