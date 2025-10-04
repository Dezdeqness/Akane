package com.dezdeqness.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseResponse(
    val id: Long,
    val type: Type,
    val year: Long,
    val name: Name,
    val alias: String,
    val season: Season,
    val poster: Poster,
    @SerialName("fresh_at")
    val freshAt: String,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("is_ongoing")
    val isOngoing: Boolean,
    @SerialName("age_rating")
    val ageRating: AgeRating,
    @SerialName("publish_day")
    val publishDay: PublishDay,
    val description: String?,
    @SerialName("episodes_total")
    val episodesTotal: Long?,
    val genres: List<Genre>,
    val episodes: List<Episode>?,
)

@Serializable
data class Type(
    val value: String?,
    val description: String?,
)

@Serializable
data class Name(
    val main: String,
    val english: String,
    val alternative: String?,
)

@Serializable
data class Season(
    val value: String,
    val description: String,
)

@Serializable
data class Poster(
    val src: String,
    val thumbnail: String,
    val optimized: Optimized,
)

@Serializable
data class Optimized(
    val src: String,
    val thumbnail: String,
)

@Serializable
data class AgeRating(
    val value: String,
    val label: String,
    @SerialName("is_adult")
    val isAdult: Boolean,
    val description: String,
)

@Serializable
data class PublishDay(
    val value: Long,
    val description: String,
)

@Serializable
data class Genre(
    val id: Long,
    val name: String,
    val image: Image,
    @SerialName("total_releases")
    val totalReleases: Long,
)

@Serializable
data class Image(
    val preview: String,
    val thumbnail: String,
    val optimized: Optimized2,
)

@Serializable
data class Optimized2(
    val preview: String?,
    val thumbnail: String,
)

// TODO: make map of hls
@Serializable
data class Episode(
    val id: String,
    val name: String?,
    val ordinal: Long,
    val opening: Opening,
    val ending: Ending,
    val preview: Preview,
    @SerialName("hls_480")
    val hls480: String?,
    @SerialName("hls_720")
    val hls720: String?,
    @SerialName("hls_1080")
    val hls1080: String?,
    val duration: Long,
    @SerialName("rutube_id")
    val rutubeId: String?,
    @SerialName("youtube_id")
    val youtubeId: String?,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("sort_order")
    val sortOrder: Long,
    @SerialName("name_english")
    val nameEnglish: String?,
)

@Serializable
data class Opening(
    val stop: Long?,
    val start: Long?,
)

@Serializable
data class Ending(
    val stop: Long?,
    val start: Long?,
)

@Serializable
data class Preview(
    val src: String,
    val thumbnail: String,
    val optimized: Optimized2,
)