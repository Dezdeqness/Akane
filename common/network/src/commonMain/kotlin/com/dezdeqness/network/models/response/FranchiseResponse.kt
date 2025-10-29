package com.dezdeqness.network.models.response

import com.dezdeqness.network.models.core.Image
import com.dezdeqness.network.models.core.release.AgeRating
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FranchiseResponse(
    val id: String,
    val name: String,
    val image: Image,
    val rating: AgeRating?,
    @SerialName("last_year")
    val lastYear: Long,
    @SerialName("first_year")
    val firstYear: Long,
    @SerialName("name_english")
    val nameEnglish: String,
    @SerialName("total_episodes")
    val totalEpisodes: Long,
    @SerialName("total_releases")
    val totalReleases: Long,
    @SerialName("total_duration")
    val totalDuration: String,
    @SerialName("total_duration_in_seconds")
    val totalDurationInSeconds: Long,
    @SerialName("franchise_releases")
    val franchiseReleases: List<FranchiseRelease>,
)

@Serializable
data class FranchiseRelease(
    val id: String,
    @SerialName("sort_order")
    val sortOrder: Long,
    @SerialName("release_id")
    val releaseId: Long,
    @SerialName("franchise_id")
    val franchiseId: String,
    val release: ReleaseResponse,
)
