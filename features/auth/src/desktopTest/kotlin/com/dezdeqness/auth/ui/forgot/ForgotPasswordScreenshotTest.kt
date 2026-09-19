package com.dezdeqness.auth.ui.forgot

import com.dezdeqness.screenshot.screenshotViewports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

class ForgotPasswordScreenshotTest {

    @Test
    fun empty() = screenshotViewports("forgot_password_empty") { _ ->
        ForgotPasswordPage(
            stateFlow = MutableStateFlow(ForgotPasswordState()),
            actions = NoOpForgotPasswordActions,
            onHaveTokenClicked = {},
            onBackToLoginClicked = {},
            onCloseClicked = {},
        )
    }

    @Test
    fun filled() = screenshotViewports("forgot_password_filled") { _ ->
        ForgotPasswordPage(
            stateFlow = MutableStateFlow(
                ForgotPasswordState(email = "danyl@example.com"),
            ),
            actions = NoOpForgotPasswordActions,
            onHaveTokenClicked = {},
            onBackToLoginClicked = {},
            onCloseClicked = {},
        )
    }

    @Test
    fun error() = screenshotViewports("forgot_password_error") { _ ->
        ForgotPasswordPage(
            stateFlow = MutableStateFlow(
                ForgotPasswordState(
                    email = "danyl@example.com",
                    errorMessage = "Пользователь с таким email не найден",
                ),
            ),
            actions = NoOpForgotPasswordActions,
            onHaveTokenClicked = {},
            onBackToLoginClicked = {},
            onCloseClicked = {},
        )
    }

    @Test
    fun loading() = screenshotViewports("forgot_password_loading") { _ ->
        ForgotPasswordPage(
            stateFlow = MutableStateFlow(
                ForgotPasswordState(email = "danyl@example.com", isLoading = true),
            ),
            actions = NoOpForgotPasswordActions,
            onHaveTokenClicked = {},
            onBackToLoginClicked = {},
            onCloseClicked = {},
        )
    }
}

private object NoOpForgotPasswordActions : ForgotPasswordActions {
    override fun onEmailChanged(value: String) = Unit
    override fun onSubmitClicked() = Unit
}
