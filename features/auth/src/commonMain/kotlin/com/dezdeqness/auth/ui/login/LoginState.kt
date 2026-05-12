package com.dezdeqness.auth.ui.login

data class LoginState(
    val login: String = "",
    val password: String = "",
    val rememberMe: Boolean = true,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
