package com.dezdeqness.stories.data.datasource

import com.dezdeqness.stories.contract.model.StoryCollectionEntity

interface StoriesApiDatasource {
    suspend fun getWeekStories(todayDayOfWeek: Int): Result<List<StoryCollectionEntity>>
}
