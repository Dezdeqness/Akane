package com.dezdeqness.feed.contract.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
import com.dezdeqness.feed.contract.model.CatalogFilter
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getFeed(
        page: Int,
        filter: CatalogFilter,
    ): Result<ReleasesPageEntity>

    fun getFeedBestRating(): Flow<Result<CachedResult<List<ReleaseEntity>>>>
    fun getFeedOngoing(): Flow<Result<CachedResult<List<ReleaseEntity>>>>
    fun getFeedReleased(): Flow<Result<CachedResult<List<ReleaseEntity>>>>
}
