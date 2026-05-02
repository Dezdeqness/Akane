package com.dezdeqness.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.home.ui.HomePageStandalone
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

fun EntryProviderScope<NavKey>.homeEntries(onItemClicked: (Long, String) -> Unit) {
    entry<HomeRoute> {
        HomePageStandalone(onItemClicked = onItemClicked)
    }
}
