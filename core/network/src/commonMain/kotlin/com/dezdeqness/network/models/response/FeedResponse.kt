package com.dezdeqness.network.models.response

import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(
    val release: ReleaseResponse? = null
)
