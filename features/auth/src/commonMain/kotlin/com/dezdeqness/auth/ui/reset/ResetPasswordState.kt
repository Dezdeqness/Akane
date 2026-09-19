package com.dezdeqness.auth.ui.reset

data class ResetPasswordState(
    val token: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmationVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)
