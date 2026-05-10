package com.dezdeqness.network.models.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenResponse(
    val token: String?,
)
