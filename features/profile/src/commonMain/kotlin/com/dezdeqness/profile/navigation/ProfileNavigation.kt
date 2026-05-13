package com.dezdeqness.profile.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.profile.ui.ProfileStandalonePage
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute : NavKey

fun EntryProviderScope<NavKey>.profileEntries() {
    entry<ProfileRoute> {
        ProfileStandalonePage()
    }
}

fun NavBackStack<NavKey>.navigateToProfile() {
    add(ProfileRoute)
}
