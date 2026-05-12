package com.dezdeqness.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.auth.ui.login.LoginStandalonePage
import com.dezdeqness.auth.ui.register.RegisterStandalonePage
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute : NavKey

@Serializable
data object RegisterRoute : NavKey

fun EntryProviderScope<NavKey>.authEntries(
    backStack: NavBackStack<NavKey>,
) {
    entry<LoginRoute> {
        LoginStandalonePage(
            onRegisterClicked = { backStack.navigateToRegister() },
        )
    }
    entry<RegisterRoute> {
        RegisterStandalonePage(
            onLoginClicked = { backStack.removeLastOrNull() },
        )
    }
}

fun NavBackStack<NavKey>.navigateToLogin() {
    add(LoginRoute)
}

fun NavBackStack<NavKey>.navigateToRegister() {
    add(RegisterRoute)
}
