package com.dezdeqness.personal.data.datasource

import com.dezdeqness.personal.contract.model.PersonalPageEntity

interface PersonalRemoteDatasource {
    suspend fun getFavoriteIds(): Result<List<Long>>
    suspend fun getFavoriteReleases(page: Int, limit: Int): Result<PersonalPageEntity>
    suspend fun addToFavorites(releaseId: Long): Result<List<Long>>
    suspend fun removeFromFavorites(releaseId: Long): Result<List<Long>>
}
