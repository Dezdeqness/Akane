package com.dezdeqness.feed.domain.repository

import com.dezdeqness.feed.domain.model.FeedEntity
import com.dezdeqness.feed.domain.model.ReleaseEntity

interface FeedRepository {
    suspend fun getFeed(page: Int): Result<FeedEntity>
    suspend fun getFeedBestRating(): Result<List<ReleaseEntity>>
    suspend fun getFeedOngoing(): Result<List<ReleaseEntity>>
    suspend fun getFeedInProduction(): Result<List<ReleaseEntity>>
}
