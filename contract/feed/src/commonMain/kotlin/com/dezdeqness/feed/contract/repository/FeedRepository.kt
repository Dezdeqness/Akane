package com.dezdeqness.feed.contract.repository

import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
import com.dezdeqness.feed.contract.model.CatalogFilter

interface FeedRepository {
    suspend fun getFeed(
        page: Int,
        filter: CatalogFilter,
    ): Result<ReleasesPageEntity>

    suspend fun getFeedBestRating(): Result<List<ReleaseEntity>>
    suspend fun getFeedOngoing(): Result<List<ReleaseEntity>>
    suspend fun getFeedReleased(): Result<List<ReleaseEntity>>
}
