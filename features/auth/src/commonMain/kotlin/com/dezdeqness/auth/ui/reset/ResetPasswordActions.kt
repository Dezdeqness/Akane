package com.dezdeqness.auth.ui.reset

interface ResetPasswordActions {
    fun onTokenChanged(value: String)
    fun onPasswordChanged(value: String)
    fun onPasswordConfirmationChanged(value: String)
    fun onPasswordVisibilityToggled()
    fun onConfirmationVisibilityToggled()
    fun onSubmitClicked()
}
