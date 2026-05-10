package com.dezdeqness.network.models.request

import kotlinx.serialization.Serializable

@Serializable
data class ForgetPasswordRequest(
    val email: String,
)
