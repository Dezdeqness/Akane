package com.dezdeqness.auth.ui.forgot

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)
