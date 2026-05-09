package com.dezdeqness.feed.contract.repository

import com.dezdeqness.feed.contract.model.CatalogFilter
import com.dezdeqness.feed.contract.model.FeedEntity
import com.dezdeqness.feed.contract.model.ReleaseEntity

interface FeedRepository {
    suspend fun getFeed(
        page: Int,
        filter: CatalogFilter,
    ): Result<FeedEntity>

    suspend fun getFeedBestRating(): Result<List<ReleaseEntity>>
    suspend fun getFeedOngoing(): Result<List<ReleaseEntity>>
    suspend fun getFeedReleased(): Result<List<ReleaseEntity>>
}
