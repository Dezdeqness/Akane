package com.dezdeqness.personal.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.personal.ui.PersonalStandalonePage
import kotlinx.serialization.Serializable

@Serializable
data object PersonalRoute : NavKey

fun EntryProviderScope<NavKey>.personalEntries(
    onItemClicked: (Long, String) -> Unit,
    onEmptyListActionClicked: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    entry<PersonalRoute> {
        PersonalStandalonePage(
            onItemClicked = onItemClicked,
            onEmptyListActionClicked = onEmptyListActionClicked,
            onNavigateToProfile = onNavigateToProfile,
        )
    }
}
