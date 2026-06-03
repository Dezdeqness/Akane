package com.dezdeqness.network.models.request

import kotlinx.serialization.Serializable

@Serializable
data class FavoritesReleasesRequest(
    val page: Int = 1,
    val limit: Int,
)
