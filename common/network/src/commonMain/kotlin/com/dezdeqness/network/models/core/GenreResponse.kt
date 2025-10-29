package com.dezdeqness.network.models.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val id: Long,
    val name: String,
    val image: Image,
    @SerialName("total_releases")
    val totalReleases: Long,
)
