package com.dezdeqness.auth.ui.login

interface LoginActions {
    fun onLoginChanged(value: String)
    fun onPasswordChanged(value: String)
    fun onPasswordVisibilityToggled()
    fun onSignInClicked()
}
