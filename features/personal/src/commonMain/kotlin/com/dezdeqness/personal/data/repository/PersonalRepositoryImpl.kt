package com.dezdeqness.personal.data.repository

import com.dezdeqness.personal.contract.model.PersonalPageEntity
import com.dezdeqness.personal.contract.repository.PersonalRepository
import com.dezdeqness.personal.data.datasource.PersonalLocalDatasource
import com.dezdeqness.personal.data.datasource.PersonalRemoteDatasource

class PersonalRepositoryImpl(
    private val localDatasource: PersonalLocalDatasource,
    private val remoteDatasource: PersonalRemoteDatasource,
) : PersonalRepository {

    override fun getFavoriteIdsAsFlow() = localDatasource.getFavoriteIdsAsFlow()

    override suspend fun containsById(id: Long) = localDatasource.contains(id)

    override suspend fun syncFavoriteIds(): Result<Unit> =
        remoteDatasource.getFavoriteIds()
            .onSuccess { ids -> localDatasource.replaceAll(ids) }
            .map { }

    override suspend fun addToFavorites(id: Long): Result<Unit> =
        remoteDatasource.addToFavorites(id)
            .onSuccess { ids -> localDatasource.replaceAll(ids) }
            .map { }

    override suspend fun removeFromFavorites(id: Long): Result<Unit> =
        remoteDatasource.removeFromFavorites(id)
            .onSuccess { ids -> localDatasource.replaceAll(ids) }
            .map { }

    override suspend fun getFavoriteReleases(page: Int): Result<PersonalPageEntity> =
        remoteDatasource.getFavoriteReleases(page = page, limit = LIMIT)

    private companion object {
        private const val LIMIT = 20
    }
}
