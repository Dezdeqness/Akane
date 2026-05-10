package com.dezdeqness.network.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val password: String,
    @SerialName("password_confirmation")
    val passwordConfirmation: String,
)
