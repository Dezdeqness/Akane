package com.dezdeqness.feed.data.datasource

import com.dezdeqness.feed.domain.model.FeedEntity

interface FeedApiDatasource {
    suspend fun getFeed(page: Int): Result<FeedEntity>
}
