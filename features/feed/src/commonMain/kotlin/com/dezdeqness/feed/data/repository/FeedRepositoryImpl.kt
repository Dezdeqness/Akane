package com.dezdeqness.feed.data.repository

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.data.mapper.CatalogFilterMapper
import com.dezdeqness.feed.contract.model.CatalogFilter
import com.dezdeqness.feed.contract.repository.FeedRepository

class FeedRepositoryImpl(
    private val feedApiDatasource: FeedApiDatasource,
    private val catalogFilterMapper: CatalogFilterMapper,
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

    override suspend fun getFeedBestRating() = feedApiDatasource.getFeedBestRating()
    override suspend fun getFeedOngoing() = feedApiDatasource.getFeedOngoing()
    override suspend fun getFeedReleased() = feedApiDatasource.getFeedReleased()

    companion object {
        private const val DEFAULT_LIMIT = 21
    }
}
