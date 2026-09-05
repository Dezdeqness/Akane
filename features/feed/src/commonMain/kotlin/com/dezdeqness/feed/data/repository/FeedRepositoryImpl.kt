package com.dezdeqness.feed.data.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.cache.staleWhileRevalidate
import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
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
    ): Result<ReleasesPageEntity> {
        val builder = catalogFilterMapper.map(filter)

        val result = feedApiDatasource.getFeed(
            page = page,
            limit = DEFAULT_LIMIT,
            queryMap = builder.buildQueryMap(),
        )

        if (isStandardRequest(page, filter)) {
            result.onSuccess { response ->
                if (response.items.isNotEmpty()) {
                    jsonCacheStore.write(
                        KEY_STANDARD_FEED,
                        response.items.map(feedCacheMapper::toSnapshot),
                        SNAPSHOT_SERIALIZER,
                    )
                }
            }
        }

        return result
    }

    override suspend fun getCachedFeed(): ReleasesPageEntity? {
        val snapshot = jsonCacheStore
            .read(KEY_STANDARD_FEED, SNAPSHOT_SERIALIZER)
            ?.takeIf { it.isNotEmpty() }
            ?: return null

        return ReleasesPageEntity(
            items = snapshot.map(feedCacheMapper::toEntity),
            currentPage = STANDARD_PAGE,
            nextPage = STANDARD_PAGE + 1,
            hasNextPage = true,
        )
    }

    private fun isStandardRequest(page: Int, filter: CatalogFilter) =
        page == STANDARD_PAGE && filter == CatalogFilter()

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
        private const val STANDARD_PAGE = 1
        private const val KEY_STANDARD_FEED = "feed_standard_first_page"
        private const val KEY_ONGOING = "feed_ongoing"
        private const val KEY_RELEASED = "feed_released"
        private const val KEY_BEST_RATING = "feed_best_rating"
        private val TTL_MILLIS = 24.hours.inWholeMilliseconds
        private val SNAPSHOT_SERIALIZER = ListSerializer(ReleaseSnapshot.serializer())
    }
}
