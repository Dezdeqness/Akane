package com.dezdeqness.genre.data.repository

import com.dezdeqness.genre.contract.repository.GenreRepository
import com.dezdeqness.genre.data.datasource.GenreApiDatasource

class GenreRepositoryImpl(
    private val genreApiDatasource: GenreApiDatasource,
) : GenreRepository {

    override suspend fun getGenres() = genreApiDatasource.getGenres()

    override suspend fun getRandomGenres(limit: Int) =
        genreApiDatasource.getRandomGenres(limit = limit)

    override suspend fun getGenreReleases(
        genreId: Int,
        page: Int,
        limit: Int,
    ) = genreApiDatasource.getGenreReleases(genreId = genreId, page = page, limit = limit)
}
