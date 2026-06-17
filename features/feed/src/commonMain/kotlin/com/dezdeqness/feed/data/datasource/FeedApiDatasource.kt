package com.dezdeqness.feed.data.datasource

import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.catalog.contract.model.ReleasesPageEntity

interface FeedApiDatasource {
    suspend fun getFeed(page: Int): Result<ReleasesPageEntity>
    suspend fun getFeedBestRating(limit: Int = 10): Result<List<ReleaseEntity>>
    suspend fun getFeedOngoing(limit: Int = 10): Result<List<ReleaseEntity>>
    suspend fun getFeedReleased(limit: Int = 10): Result<List<ReleaseEntity>>
    suspend fun getFeed(
        page: Int,
        limit: Int,
        queryMap: Map<String, Any>,
    ): Result<ReleasesPageEntity>
}
