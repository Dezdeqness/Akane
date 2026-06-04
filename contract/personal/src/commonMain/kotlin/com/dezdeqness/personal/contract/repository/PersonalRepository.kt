package com.dezdeqness.personal.contract.repository

import com.dezdeqness.personal.contract.model.PersonalPageEntity
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    fun getFavoriteIdsAsFlow(): Flow<List<Long>>
    suspend fun containsById(id: Long): Boolean
    suspend fun syncFavoriteIds(): Result<Unit>
    suspend fun addToFavorites(id: Long): Result<Unit>
    suspend fun removeFromFavorites(id: Long): Result<Unit>
    suspend fun getFavoriteReleases(page: Int): Result<PersonalPageEntity>
}
