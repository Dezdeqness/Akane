package com.dezdeqness.network.models.response

import com.dezdeqness.network.models.core.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FranchiseItemResponse(
    val id: String,
    val name: String,
    @SerialName("name_english")
    val nameEnglish: String? = null,
    val image: Image,
    val rating: Double? = null,
    @SerialName("first_year")
    val firstYear: Long? = null,
    @SerialName("last_year")
    val lastYear: Long? = null,
    @SerialName("total_releases")
    val totalReleases: Long? = null,
    @SerialName("total_episodes")
    val totalEpisodes: Long? = null,
    @SerialName("total_duration")
    val totalDuration: String? = null,
    @SerialName("total_duration_in_seconds")
    val totalDurationInSeconds: Long? = null,
)

@Serializable
data class FranchiseDetailResponse(
    val id: String,
    val name: String,
    @SerialName("name_english")
    val nameEnglish: String? = null,
    val image: Image,
    val rating: Double? = null,
    @SerialName("first_year")
    val firstYear: Long? = null,
    @SerialName("last_year")
    val lastYear: Long? = null,
    @SerialName("total_releases")
    val totalReleases: Long? = null,
    @SerialName("total_episodes")
    val totalEpisodes: Long? = null,
    @SerialName("total_duration")
    val totalDuration: String? = null,
    @SerialName("total_duration_in_seconds")
    val totalDurationInSeconds: Long? = null,
    @SerialName("franchise_releases")
    val franchiseReleases: List<FranchiseReleaseItemResponse> = emptyList(),
)

@Serializable
data class FranchiseReleaseItemResponse(
    val id: String,
    @SerialName("sort_order")
    val sortOrder: Long = 0,
    @SerialName("release_id")
    val releaseId: Long,
    @SerialName("franchise_id")
    val franchiseId: String,
    val release: ReleaseResponse,
)
