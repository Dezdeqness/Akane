package com.dezdeqness.details.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDetailsSnapshot(
    val id: Long,
    val name: String,
    val poster: String,
    val type: String,
    val season: String,
    val description: String,
    val episodesTotal: Long,
    val genres: List<String>,
    val episodes: List<EpisodeSnapshot>,
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

@Serializable
data class EpisodeSnapshot(
    val id: String,
    val name: String,
    val previewUrl: String,
    val ordinal: Long,
    val episodeUrls: Map<String, String>,
    val duration: Long,
    val updatedAt: String,
    val nameEnglish: String?,
    val opening: TimingSnapshot?,
    val ending: TimingSnapshot?,
)

@Serializable
data class TimingSnapshot(
    val start: Long,
    val end: Long,
)
