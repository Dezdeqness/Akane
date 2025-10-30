package com.dezdeqness.details.domain.model

// TODO: shared
data class ReleaseDetailsEntity(
    val id: Long,
    val name: String,
    val poster: String,
    val type: String,
    val season: String,
    val description: String,
    val episodesTotal: Long,
    val genres: List<String>,
    val episodes: List<EpisodeEntity>,
    val year: Long,
    val isOngoing: Boolean,
    val ageRating: String,
    val userFavourites: Long,
    val averageDuration: Long?,
    val planned: Long,
    val watched: Long,
    val watching: Long,
    val postponed: Long,
    val abandoned: Long,
)
