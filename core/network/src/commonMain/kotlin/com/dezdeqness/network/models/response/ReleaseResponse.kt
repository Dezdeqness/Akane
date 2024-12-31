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
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("is_ongoing")
    val isOngoing: Boolean,
    @SerialName("age_rating")
    val ageRating: AgeRating,
    @SerialName("publish_day")
    val publishDay: PublishDay,
    val description: String?,
)

@Serializable
data class Type(
    val value: String,
    val description: String,
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
    val preview: String,
    val thumbnail: String,
)