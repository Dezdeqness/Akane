package com.dezdeqness.genre.contract.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
import com.dezdeqness.genre.contract.model.GenreEntity
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    suspend fun getGenres(): Result<List<GenreEntity>>

    fun getRandomGenres(limit: Int = 10): Flow<Result<CachedResult<List<GenreEntity>>>>

    suspend fun getGenreReleases(
        genreId: Int,
        page: Int,
        limit: Int,
    ): Result<ReleasesPageEntity>
}
