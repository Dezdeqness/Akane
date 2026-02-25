package com.dezdeqness.details.domain.model

data class EpisodeEntity(
    val id: String,
    val name: String,
    val previewUrl: String,
    val ordinal: Long,
    val episodeUrls: LinkedHashMap<VideoQuality, String>,
    val duration: Long,
    val updatedAt: String,
    val nameEnglish: String?,
    val opening: TimingEntity?,
    val ending: TimingEntity?,
)

data class TimingEntity(
    val start: Long,
    val end: Long,
)
