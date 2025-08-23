package com.dezdeqness.personal.domain.repository

import com.dezdeqness.personal.domain.models.PersonalEntity
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    fun getPersonalAsFlow(): Flow<List<PersonalEntity>>
    suspend fun containsById(id: Long): Boolean
    suspend fun getPersonalList(): List<PersonalEntity>
    suspend fun deleteById(id: Long)
    suspend fun add(item: PersonalEntity)
}
