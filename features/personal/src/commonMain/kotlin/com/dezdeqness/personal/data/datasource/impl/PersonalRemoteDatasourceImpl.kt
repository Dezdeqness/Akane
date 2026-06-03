package com.dezdeqness.personal.data.datasource.impl

import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.models.request.FavoriteReleaseRequest
import com.dezdeqness.network.models.request.FavoritesReleasesRequest
import com.dezdeqness.network.services.FavoritesService
import com.dezdeqness.personal.contract.model.PersonalEntity
import com.dezdeqness.personal.data.datasource.PersonalRemoteDatasource
import com.dezdeqness.personal.data.mapper.PersonalMapper

class PersonalRemoteDatasourceImpl(
    private val favoritesService: FavoritesService,
    private val personalMapper: PersonalMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), PersonalRemoteDatasource {

    override suspend fun getFavoriteIds(): Result<List<Long>> = tryWithCatchSuspend {
        val response = favoritesService.getFavoriteIds()
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty())
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun getFavoriteReleases(
        page: Int,
        limit: Int,
    ): Result<List<PersonalEntity>> = tryWithCatchSuspend {
        val response = favoritesService.getFavoriteReleases(
            FavoritesReleasesRequest(page = page, limit = limit),
        )
        if (response.isSuccessful) {
            val releases = response.body()?.data.orEmpty()
            Result.success(releases.map(personalMapper::fromRelease))
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun addToFavorites(releaseId: Long): Result<List<Long>> = tryWithCatchSuspend {
        val response = favoritesService.addToFavorites(
            listOf(FavoriteReleaseRequest(releaseId = releaseId)),
        )
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty())
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun removeFromFavorites(releaseId: Long): Result<List<Long>> = tryWithCatchSuspend {
        val response = favoritesService.removeFromFavorites(
            listOf(FavoriteReleaseRequest(releaseId = releaseId)),
        )
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty())
        } else {
            throw response.createApiException()
        }
    }
}
