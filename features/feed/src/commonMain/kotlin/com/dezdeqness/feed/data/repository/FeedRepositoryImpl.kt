package com.dezdeqness.feed.data.repository

import com.dezdeqness.feed.data.datasource.FeedApiDatasource
import com.dezdeqness.feed.domain.model.ReleaseEntity
import com.dezdeqness.feed.domain.repository.FeedRepository

class FeedRepositoryImpl(
    private val feedApiDatasource: FeedApiDatasource,
) : FeedRepository {
    override suspend fun getFeed(page: Int) = feedApiDatasource.getFeed(page)
    override suspend fun getFeedBestRating() = feedApiDatasource.getFeedBestRating()
    override suspend fun getFeedOngoing() = feedApiDatasource.getFeedOngoing()
    override suspend fun getFeedInProduction() = feedApiDatasource.getFeedInProduction()
}
