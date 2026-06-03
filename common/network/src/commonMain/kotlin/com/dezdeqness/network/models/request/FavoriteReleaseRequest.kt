package com.dezdeqness.network.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteReleaseRequest(
    @SerialName("release_id")
    val releaseId: Long,
)
