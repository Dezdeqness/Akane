package com.dezdeqness.downloads.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.downloads.ui.activedownloads.ActiveDownloadsPage
import com.dezdeqness.downloads.ui.episodes.ReleaseEpisodesPage
import com.dezdeqness.downloads.ui.episodes.ReleaseEpisodesViewModel
import com.dezdeqness.downloads.ui.library.LibraryPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object DownloadsRoute : NavKey

@Serializable
data class ReleaseEpisodesRoute(val releaseId: Long) : NavKey

@Serializable
data object ActiveDownloadsRoute : NavKey

fun EntryProviderScope<NavKey>.downloadsEntries(
    onReleaseClicked: (releaseId: Long) -> Unit,
    activeDownloadsCountFlow: Flow<Int> = emptyFlow(),
    onActiveDownloadsClicked: () -> Unit = {},
) {
    entry<DownloadsRoute> {
        LibraryPage(
            onReleaseClicked = onReleaseClicked,
            activeDownloadsCountFlow = activeDownloadsCountFlow,
            onActiveDownloadsClicked = onActiveDownloadsClicked,
        )
    }
}

fun EntryProviderScope<NavKey>.releaseEpisodesEntries(
    onBackPressed: () -> Unit,
    onPlayClicked: (releaseId: Long, episodeId: String) -> Unit,
) {
    entry<ReleaseEpisodesRoute> { key ->
        val viewModel: ReleaseEpisodesViewModel = koinViewModel { parametersOf(key.releaseId) }
        ReleaseEpisodesPage(
            viewModel = viewModel,
            onPlayClicked = onPlayClicked,
            onBackPressed = onBackPressed,
        )
    }
}

fun EntryProviderScope<NavKey>.activeDownloadsEntries(
    onBackPressed: () -> Unit,
) {
    entry<ActiveDownloadsRoute> {
        ActiveDownloadsPage(onBackPressed = onBackPressed)
    }
}

fun NavBackStack<NavKey>.navigateToReleaseEpisodes(releaseId: Long) {
    add(ReleaseEpisodesRoute(releaseId))
}

fun NavBackStack<NavKey>.navigateToActiveDownloads() {
    add(ActiveDownloadsRoute)
}
