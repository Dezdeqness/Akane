package com.dezdeqness.auth.ui.reset

import com.dezdeqness.screenshot.screenshotViewports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

class ResetPasswordScreenshotTest {

    @Test
    fun empty() = screenshotViewports("reset_password_empty") { _ ->
        ResetPasswordPage(
            stateFlow = MutableStateFlow(ResetPasswordState()),
            actions = NoOpResetPasswordActions,
            onBackToLoginClicked = {},
            onCloseClicked = {},
        )
    }

    @Test
    fun filled() = screenshotViewports("reset_password_filled") { _ ->
        ResetPasswordPage(
            stateFlow = MutableStateFlow(
                ResetPasswordState(
                    token = "a1b2c3d4",
                    password = "secret123",
                    passwordConfirmation = "secret123",
                ),
            ),
            actions = NoOpResetPasswordActions,
            onBackToLoginClicked = {},
            onCloseClicked = {},
        )
    }

    @Test
    fun error() = screenshotViewports("reset_password_error") { _ ->
        ResetPasswordPage(
            stateFlow = MutableStateFlow(
                ResetPasswordState(
                    token = "a1b2c3d4",
                    password = "secret123",
                    passwordConfirmation = "secret999",
                    errorMessage = "Пароли не совпадают",
                ),
            ),
            actions = NoOpResetPasswordActions,
            onBackToLoginClicked = {},
            onCloseClicked = {},
        )
    }

    @Test
    fun loading() = screenshotViewports("reset_password_loading") { _ ->
        ResetPasswordPage(
            stateFlow = MutableStateFlow(
                ResetPasswordState(
                    token = "a1b2c3d4",
                    password = "secret123",
                    passwordConfirmation = "secret123",
                    isLoading = true,
                ),
            ),
            actions = NoOpResetPasswordActions,
            onBackToLoginClicked = {},
            onCloseClicked = {},
        )
    }
}

private object NoOpResetPasswordActions : ResetPasswordActions {
    override fun onTokenChanged(value: String) = Unit
    override fun onPasswordChanged(value: String) = Unit
    override fun onPasswordConfirmationChanged(value: String) = Unit
    override fun onPasswordVisibilityToggled() = Unit
    override fun onConfirmationVisibilityToggled() = Unit
    override fun onSubmitClicked() = Unit
}
