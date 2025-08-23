package com.dezdeqness.personal.data.datasource

import com.dezdeqness.personal.domain.models.PersonalEntity
import kotlinx.coroutines.flow.Flow

interface PersonalDatasource {
    fun getPersonalAsFlow(): Flow<List<PersonalEntity>>
    suspend fun containsById(id: Long): Boolean
    suspend fun getPersonalList(): List<PersonalEntity>
    suspend fun deleteById(id: Long)
    suspend fun add(item: PersonalEntity)
}
