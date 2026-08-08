package com.dezdeqness.home.domain

data class ContinueWatchingEntity(
    val releaseId: Long,
    val episodeId: String,
    val releaseTitle: String,
    val episodeName: String,
    val episodeOrdinal: Long,
    val previewUrl: String,
)
