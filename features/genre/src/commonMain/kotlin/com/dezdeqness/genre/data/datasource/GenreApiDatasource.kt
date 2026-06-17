package com.dezdeqness.genre.data.datasource

import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
import com.dezdeqness.genre.contract.model.GenreEntity

interface GenreApiDatasource {
    suspend fun getGenres(): Result<List<GenreEntity>>

    suspend fun getRandomGenres(limit: Int): Result<List<GenreEntity>>

    suspend fun getGenreReleases(
        genreId: Int,
        page: Int,
        limit: Int,
    ): Result<ReleasesPageEntity>
}
