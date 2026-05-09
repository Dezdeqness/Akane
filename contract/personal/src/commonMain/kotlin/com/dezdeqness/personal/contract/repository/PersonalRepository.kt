package com.dezdeqness.personal.contract.repository

import com.dezdeqness.personal.contract.model.PersonalEntity
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    fun getPersonalAsFlow(): Flow<List<PersonalEntity>>
    suspend fun containsById(id: Long): Boolean
    suspend fun getPersonalList(): List<PersonalEntity>
    suspend fun deleteById(id: Long)
    suspend fun add(item: PersonalEntity)
}
