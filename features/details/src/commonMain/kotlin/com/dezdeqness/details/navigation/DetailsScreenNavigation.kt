package com.dezdeqness.details.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.details.ui.DetailsPage
import com.dezdeqness.details.ui.ReleaseDetailsViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class DetailsRoute(val releaseId: Long) : NavKey

fun EntryProviderScope<NavKey>.detailsEntries(
    onBackPressed: () -> Unit,
    onEpisodeClick: (Long, String) -> Unit,
    onReleaseClicked: (Long, String) -> Unit,
) {
    entry<DetailsRoute> { key ->
        val viewModel: ReleaseDetailsViewModel = koinViewModel { parametersOf(key.releaseId) }
        DetailsPage(
            viewModel = viewModel,
            onBackPressed = onBackPressed,
            onEpisodeClick = onEpisodeClick,
            onReleaseClicked = onReleaseClicked,
        )
    }
}

fun NavBackStack<NavKey>.navigateToDetailsScreen(releaseId: Long) {
    add(DetailsRoute(releaseId))
}
