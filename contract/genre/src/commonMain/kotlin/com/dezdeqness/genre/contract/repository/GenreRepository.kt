package com.dezdeqness.genre.contract.repository

import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.genre.contract.model.GenreReleasesEntity

interface GenreRepository {
    suspend fun getGenres(): Result<List<GenreEntity>>

    suspend fun getRandomGenres(limit: Int): Result<List<GenreEntity>>

    suspend fun getGenreReleases(
        genreId: Int,
        page: Int,
        limit: Int,
    ): Result<GenreReleasesEntity>
}
