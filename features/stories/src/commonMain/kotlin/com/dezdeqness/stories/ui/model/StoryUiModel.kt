package com.dezdeqness.stories.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class StoryCollectionUiModel(
    val id: Int,
    val title: String,
    val stories: List<StoryUiModel>,
)

data class StoryUiModel(
    val releaseId: Long,
    val releaseName: String,
    val poster: String,
    val episodeId: String,
    val episodeLabel: String,
    val previewUrl: String,
    val videoUrl: String,
)
