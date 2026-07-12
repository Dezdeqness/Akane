package com.dezdeqness.genre.data.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.cache.staleWhileRevalidate
import com.dezdeqness.foundation.cache.JsonCacheStore
import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.genre.contract.repository.GenreRepository
import com.dezdeqness.genre.data.cache.GenreCacheMapper
import com.dezdeqness.genre.data.cache.GenreSnapshot
import com.dezdeqness.genre.data.datasource.GenreApiDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer
import kotlin.time.Duration.Companion.hours

class GenreRepositoryImpl(
    private val genreApiDatasource: GenreApiDatasource,
    private val jsonCacheStore: JsonCacheStore,
    private val genreCacheMapper: GenreCacheMapper,
) : GenreRepository {

    override suspend fun getGenres() = genreApiDatasource.getGenres()

    override fun getRandomGenres(limit: Int): Flow<Result<CachedResult<List<GenreEntity>>>> =
        staleWhileRevalidate(
            read = {
                jsonCacheStore
                    .read(RANDOM_KEY, SNAPSHOT_SERIALIZER, TTL_MILLIS)
                    ?.map(genreCacheMapper::toEntity)
            },
            fetch = { genreApiDatasource.getRandomGenres(limit = limit) },
            write = { jsonCacheStore.write(RANDOM_KEY, it.map(genreCacheMapper::toSnapshot), SNAPSHOT_SERIALIZER) },
        )

    override suspend fun getGenreReleases(
        genreId: Int,
        page: Int,
        limit: Int,
    ) = genreApiDatasource.getGenreReleases(genreId = genreId, page = page, limit = limit)

    private companion object {
        const val RANDOM_KEY = "genre_random"
        val TTL_MILLIS = 24.hours.inWholeMilliseconds
        val SNAPSHOT_SERIALIZER = ListSerializer(GenreSnapshot.serializer())
    }
}
