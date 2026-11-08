package com.dezdeqness.stories.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class StoryCollectionSnapshot(
    val dayOfWeek: Int,
    val dayOffset: Int,
    val stories: List<StorySnapshot>,
)

@Serializable
data class StorySnapshot(
    val releaseId: Long,
    val releaseName: String,
    val poster: String,
    val episodeId: String,
    val episodeOrdinal: Double,
    val episodeName: String,
    val previewUrl: String,
    val videoUrl: String,
)

