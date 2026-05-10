package com.dezdeqness.network.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val login: String,
    val email: String,
    val nickname: String,
    val password: String,
    @SerialName("password_confirmation")
    val passwordConfirmation: String,
    @SerialName("recaptcha_token")
    val recaptchaToken: String,
)
