package com.dezdeqness.genre.contract.repository

import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
import com.dezdeqness.genre.contract.model.GenreEntity

interface GenreRepository {
    suspend fun getGenres(): Result<List<GenreEntity>>

    suspend fun getRandomGenres(limit: Int = 10): Result<List<GenreEntity>>

    suspend fun getGenreReleases(
        genreId: Int,
        page: Int,
        limit: Int,
    ): Result<ReleasesPageEntity>
}
