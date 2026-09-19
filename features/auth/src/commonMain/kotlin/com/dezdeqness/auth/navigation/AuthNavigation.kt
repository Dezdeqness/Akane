package com.dezdeqness.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.auth.ui.forgot.ForgotPasswordStandalonePage
import com.dezdeqness.auth.ui.login.LoginStandalonePage
import com.dezdeqness.auth.ui.register.RegisterStandalonePage
import com.dezdeqness.auth.ui.reset.ResetPasswordStandalonePage
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute : NavKey

@Serializable
data object RegisterRoute : NavKey

@Serializable
data object ForgotPasswordRoute : NavKey

@Serializable
data object ResetPasswordRoute : NavKey

fun EntryProviderScope<NavKey>.authEntries(
    backStack: NavBackStack<NavKey>,
) {
    entry<LoginRoute> {
        LoginStandalonePage(
            onForgotPasswordClicked = { backStack.navigateToForgotPassword() },
        )
    }
    entry<RegisterRoute> {
        RegisterStandalonePage(
            onLoginClicked = { backStack.removeLastOrNull() },
        )
    }
    entry<ForgotPasswordRoute> {
        ForgotPasswordStandalonePage(
            onEmailSent = { backStack.navigateToResetPassword() },
            onHaveTokenClicked = { backStack.navigateToResetPassword() },
            onBackToLoginClicked = { backStack.removeLastOrNull() },
            onCloseClicked = { backStack.popToRoot() },
        )
    }
    entry<ResetPasswordRoute> {
        ResetPasswordStandalonePage(
            onPasswordReset = { backStack.popToRoot() },
            onBackToLoginClicked = { backStack.popToRoot() },
            onCloseClicked = { backStack.popToRoot() },
        )
    }
}

fun NavBackStack<NavKey>.navigateToLogin() {
    add(LoginRoute)
}

fun NavBackStack<NavKey>.navigateToRegister() {
    add(RegisterRoute)
}

fun NavBackStack<NavKey>.navigateToForgotPassword() {
    add(ForgotPasswordRoute)
}

fun NavBackStack<NavKey>.navigateToResetPassword() {
    add(ResetPasswordRoute)
}

/** Drops every pushed auth screen so the user lands back on the tab root (profile/login). */
private fun NavBackStack<NavKey>.popToRoot() {
    while (size > 1) {
        removeLastOrNull()
    }
}
