package com.dezdeqness.auth.contract.model

data class RegistrationDataEntity(
    val login: String,
    val email: String,
    val nickname: String,
    val password: String,
    val passwordConfirmation: String,
    val recaptchaToken: String,
)
