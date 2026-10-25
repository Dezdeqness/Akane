package com.dezdeqness.stories.contract.model

data class StoryCollectionEntity(
    val dayOfWeek: Int,
    val dayOffset: Int,
    val stories: List<StoryEntity>,
)
