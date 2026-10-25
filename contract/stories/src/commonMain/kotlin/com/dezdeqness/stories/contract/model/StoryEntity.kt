package com.dezdeqness.stories.contract.model

data class StoryEntity(
    val releaseId: Long,
    val releaseName: String,
    val poster: String,
    val episodeId: String,
    val episodeOrdinal: Long,
    val episodeName: String,
    val previewUrl: String,
    val videoUrl: String,
)
