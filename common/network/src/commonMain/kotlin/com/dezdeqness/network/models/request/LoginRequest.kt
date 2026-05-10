package com.dezdeqness.network.models.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val login: String,
    val password: String,
)
