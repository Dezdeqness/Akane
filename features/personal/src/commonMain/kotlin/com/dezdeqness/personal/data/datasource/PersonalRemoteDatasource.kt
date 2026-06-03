package com.dezdeqness.personal.data.datasource

import com.dezdeqness.personal.contract.model.PersonalEntity

interface PersonalRemoteDatasource {
    suspend fun getFavoriteIds(): Result<List<Long>>
    suspend fun getFavoriteReleases(page: Int = 1, limit: Int): Result<List<PersonalEntity>>
    suspend fun addToFavorites(releaseId: Long): Result<List<Long>>
    suspend fun removeFromFavorites(releaseId: Long): Result<List<Long>>
}
