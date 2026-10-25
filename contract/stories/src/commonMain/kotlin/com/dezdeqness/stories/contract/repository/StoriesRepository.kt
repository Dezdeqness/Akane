package com.dezdeqness.stories.contract.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.stories.contract.model.StoryCollectionEntity
import kotlinx.coroutines.flow.Flow

interface StoriesRepository {
    suspend fun getStories(forceRefresh: Boolean = false): Flow<Result<CachedResult<List<StoryCollectionEntity>>>>
}
