package com.dezdeqness.auth.contract.model

data class PasswordResetDataEntity(
    val token: String,
    val password: String,
    val passwordConfirmation: String,
)
