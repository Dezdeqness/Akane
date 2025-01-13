package com.dezdeqness.details.domain.model

data class EpisodeEntity(
    val id: String,
    val name: String,
    val ordinal: Long,
    val hls480: String?,
    val hls720: String?,
    val hls1080: String?,
    val duration: Long,
    val updatedAt: String,
    val nameEnglish: String?,
)
