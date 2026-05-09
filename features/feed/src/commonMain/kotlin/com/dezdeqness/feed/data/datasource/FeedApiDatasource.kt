package com.dezdeqness.feed.data.datasource

import com.dezdeqness.feed.contract.model.FeedEntity
import com.dezdeqness.feed.contract.model.ReleaseEntity

interface FeedApiDatasource {
    suspend fun getFeed(page: Int): Result<FeedEntity>
    suspend fun getFeedBestRating(limit: Int = 10): Result<List<ReleaseEntity>>
    suspend fun getFeedOngoing(limit: Int = 10): Result<List<ReleaseEntity>>
    suspend fun getFeedReleased(limit: Int = 10): Result<List<ReleaseEntity>>
    suspend fun getFeed(
        page: Int,
        limit: Int,
        queryMap: Map<String, Any>,
    ): Result<FeedEntity>
}
