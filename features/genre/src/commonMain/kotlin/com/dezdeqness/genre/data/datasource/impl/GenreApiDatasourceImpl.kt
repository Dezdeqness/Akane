package com.dezdeqness.genre.data.datasource.impl

import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
import com.dezdeqness.genre.data.datasource.GenreApiDatasource
import com.dezdeqness.genre.data.mapper.GenreMapper
import com.dezdeqness.genre.data.mapper.GenreReleaseMapper
import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.GenreService

class GenreApiDatasourceImpl(
    private val genreService: GenreService,
    private val genreMapper: GenreMapper,
    private val genreReleaseMapper: GenreReleaseMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), GenreApiDatasource {

    override suspend fun getGenres() = tryWithCatchSuspend {
        val response = genreService.getGenres()
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty().map(genreMapper::map))
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun getRandomGenres(limit: Int) = tryWithCatchSuspend {
        val response = genreService.getRandomGenres(limit = limit)
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty().map(genreMapper::map))
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun getGenreReleases(
        genreId: Int,
        page: Int,
        limit: Int,
    ) = tryWithCatchSuspend {
        val response = genreService.getGenreReleases(id = genreId, page = page, limit = limit)
        if (response.isSuccessful) {
            val body = response.body()

            val items = body
                ?.data
                .orEmpty()
                .map(genreReleaseMapper::map)

            val currentPage = body?.meta?.pagination?.currentPage ?: 0
            val totalPages = body?.meta?.pagination?.totalPages ?: 0

            Result.success(
                ReleasesPageEntity(
                    items = items,
                    currentPage = currentPage,
                    nextPage = currentPage + 1,
                    hasNextPage = currentPage < totalPages,
                )
            )
        } else {
            throw response.createApiException()
        }
    }
}
