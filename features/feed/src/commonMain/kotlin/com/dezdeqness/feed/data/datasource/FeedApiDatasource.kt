package com.dezdeqness.feed.data.datasource

import com.dezdeqness.feed.domain.model.FeedEntity
import com.dezdeqness.feed.domain.model.ReleaseEntity

interface FeedApiDatasource {
    suspend fun getFeed(page: Int): Result<FeedEntity>
    suspend fun getFeedBestRating(limit: Int = 10): Result<List<ReleaseEntity>>
    suspend fun getFeedOngoing(limit: Int = 10): Result<List<ReleaseEntity>>
    suspend fun getFeedInProduction(limit: Int = 10): Result<List<ReleaseEntity>>
}
