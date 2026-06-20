package com.dezdeqness.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.home.ui.HomePageStandalone
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

fun EntryProviderScope<NavKey>.homeEntries(
    onItemClicked: (Long, String) -> Unit,
    onGenreClicked: (Int, String) -> Unit,
    onAllGenresClicked: () -> Unit,
    onFranchiseClicked: (String, String) -> Unit,
    onAllFranchisesClicked: () -> Unit,
) {
    entry<HomeRoute> {
        HomePageStandalone(
            onItemClicked = onItemClicked,
            onGenreClicked = onGenreClicked,
            onAllGenresClicked = onAllGenresClicked,
            onFranchiseClicked = onFranchiseClicked,
            onAllFranchisesClicked = onAllFranchisesClicked,
        )
    }
}
