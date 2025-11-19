package com.dezdeqness.feed.data.repository

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.mapper.CatalogFilterMapper
import com.dezdeqness.feed.domain.model.CatalogFilter
import com.dezdeqness.feed.domain.repository.FeedRepository

class FeedRepositoryImpl(
    private val feedApiDatasource: FeedApiDatasource,
    private val catalogFilterMapper: CatalogFilterMapper,
) : FeedRepository {

    override suspend fun getFeed(page: Int) =
        getFeed(
            CatalogFilter(
                page = page,
                limit = DEFAULT_LIMIT,
            )
        )

    override suspend fun getFeed(filter: CatalogFilter) = run {
        val builder = catalogFilterMapper.map(filter)

        val page = builder.page ?: filter.page ?: 1
        val limit = builder.limit ?: filter.limit ?: DEFAULT_LIMIT

        feedApiDatasource.getFeed(
            page = page,
            limit = limit,
            queryMap = builder.buildQueryMap(),
        )
    }

    override suspend fun getFeedBestRating() = feedApiDatasource.getFeedBestRating()
    override suspend fun getFeedOngoing() = feedApiDatasource.getFeedOngoing()
    override suspend fun getFeedReleased() = feedApiDatasource.getFeedReleased()

    companion object {
        private const val DEFAULT_LIMIT = 15
    }
}
