package com.dezdeqness.details.domain.model

// TODO: shared
data class ReleaseDetailsEntity(
    val id: Long,
    val name: String,
    val poster: String,
    val type: String,
    val description: String,
    val episodesTotal: Long,
    val genres: List<String>,
    val episodes: List<EpisodeEntity>,
)
