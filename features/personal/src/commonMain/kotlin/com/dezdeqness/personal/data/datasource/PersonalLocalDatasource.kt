package com.dezdeqness.personal.data.datasource

import kotlinx.coroutines.flow.Flow

interface PersonalLocalDatasource {
    fun getFavoriteIdsAsFlow(): Flow<List<Long>>
    suspend fun contains(id: Long): Boolean
    suspend fun replaceAll(ids: List<Long>)
    suspend fun add(id: Long)
    suspend fun delete(id: Long)
}
