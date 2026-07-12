package com.dezdeqness.feed.data.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.cache.staleWhileRevalidate
import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.feed.contract.model.CatalogFilter
import com.dezdeqness.feed.contract.repository.FeedRepository
import com.dezdeqness.feed.data.cache.FeedCacheMapper
import com.dezdeqness.feed.data.cache.ReleaseSnapshot
import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.mapper.CatalogFilterMapper
import com.dezdeqness.foundation.cache.JsonCacheStore
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer
import kotlin.time.Duration.Companion.hours

class FeedRepositoryImpl(
    private val feedApiDatasource: FeedApiDatasource,
    private val catalogFilterMapper: CatalogFilterMapper,
    private val jsonCacheStore: JsonCacheStore,
    private val feedCacheMapper: FeedCacheMapper,
) : FeedRepository {

    override suspend fun getFeed(
        page: Int,
        filter: CatalogFilter,
    ) = run {
        val builder = catalogFilterMapper.map(filter)

        feedApiDatasource.getFeed(
            page = page,
            limit = DEFAULT_LIMIT,
            queryMap = builder.buildQueryMap(),
        )
    }

    override fun getFeedBestRating() =
        cachedFlow(KEY_BEST_RATING) { feedApiDatasource.getFeedBestRating() }

    override fun getFeedOngoing() =
        cachedFlow(KEY_ONGOING) { feedApiDatasource.getFeedOngoing() }

    override fun getFeedReleased() =
        cachedFlow(KEY_RELEASED) { feedApiDatasource.getFeedReleased() }

    private fun cachedFlow(
        key: String,
        fetch: suspend () -> Result<List<ReleaseEntity>>,
    ): Flow<Result<CachedResult<List<ReleaseEntity>>>> =
        staleWhileRevalidate(
            read = {
                jsonCacheStore
                    .read(key, SNAPSHOT_SERIALIZER, TTL_MILLIS)
                    ?.map(feedCacheMapper::toEntity)
            },
            fetch = fetch,
            write = { jsonCacheStore.write(key, it.map(feedCacheMapper::toSnapshot), SNAPSHOT_SERIALIZER) },
        )

    companion object {
        private const val DEFAULT_LIMIT = 21
        private const val KEY_ONGOING = "feed_ongoing"
        private const val KEY_RELEASED = "feed_released"
        private const val KEY_BEST_RATING = "feed_best_rating"
        private val TTL_MILLIS = 24.hours.inWholeMilliseconds
        private val SNAPSHOT_SERIALIZER = ListSerializer(ReleaseSnapshot.serializer())
    }
}
