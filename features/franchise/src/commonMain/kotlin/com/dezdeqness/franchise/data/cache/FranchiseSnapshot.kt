package com.dezdeqness.franchise.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class FranchiseSnapshot(
    val id: String,
    val name: String,
    val nameEnglish: String,
    val imageUrl: String,
    val rating: Double,
    val firstYear: Int,
    val lastYear: Int,
    val totalReleases: Int,
    val totalEpisodes: Int,
    val totalDuration: String?,
    val totalDurationInSeconds: Int,
)
