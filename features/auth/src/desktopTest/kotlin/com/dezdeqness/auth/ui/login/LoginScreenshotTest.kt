package com.dezdeqness.auth.ui.login

import com.dezdeqness.screenshot.screenshotViewports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

class LoginScreenshotTest {

    @Test
    fun empty() = screenshotViewports("login_empty") { _ ->
        LoginPage(
            stateFlow = MutableStateFlow(LoginState()),
            actions = NoOpLoginActions,
        )
    }

    @Test
    fun filled() = screenshotViewports("login_filled") { _ ->
        LoginPage(
            stateFlow = MutableStateFlow(
                LoginState(login = "danyl", password = "secret123"),
            ),
            actions = NoOpLoginActions,
        )
    }

    @Test
    fun error() = screenshotViewports("login_error") { _ ->
        LoginPage(
            stateFlow = MutableStateFlow(
                LoginState(
                    login = "danyl",
                    password = "wrong",
                    errorMessage = "Неверный логин или пароль",
                ),
            ),
            actions = NoOpLoginActions,
        )
    }

    @Test
    fun loading() = screenshotViewports("login_loading") { _ ->
        LoginPage(
            stateFlow = MutableStateFlow(
                LoginState(login = "danyl", password = "secret123", isLoading = true),
            ),
            actions = NoOpLoginActions,
        )
    }
}

private object NoOpLoginActions : LoginActions {
    override fun onLoginChanged(value: String) = Unit
    override fun onPasswordChanged(value: String) = Unit
    override fun onPasswordVisibilityToggled() = Unit
    override fun onSignInClicked() = Unit
}
