package com.dezdeqness.downloads.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dezdeqness.downloads.ui.activedownloads.ActiveDownloadsPage
import com.dezdeqness.downloads.ui.episodes.ReleaseEpisodesPage
import com.dezdeqness.downloads.ui.library.LibraryPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

const val DOWNLOADS_ROUTE = "downloads_route"

const val RELEASE_ID_ARG = "releaseId"
const val RELEASE_EPISODES_ROUTE = "release_episodes_route/{$RELEASE_ID_ARG}"

const val ACTIVE_DOWNLOADS_ROUTE = "active_downloads_route"

fun NavGraphBuilder.downloadsScreen(
    onReleaseClicked: (releaseId: Long) -> Unit,
    activeDownloadsCountFlow: Flow<Int> = emptyFlow(),
    onActiveDownloadsClicked: () -> Unit = {},
) {
    composable(DOWNLOADS_ROUTE) {
        LibraryPage(
            onReleaseClicked = onReleaseClicked,
            activeDownloadsCountFlow = activeDownloadsCountFlow,
            onActiveDownloadsClicked = onActiveDownloadsClicked,
        )
    }
}

fun NavGraphBuilder.releaseEpisodesScreen(
    onBackPressed: () -> Unit,
    onPlayClicked: (releaseId: Long, episodeId: String) -> Unit,
) {
    composable(
        route = RELEASE_EPISODES_ROUTE,
        arguments = listOf(
            navArgument(RELEASE_ID_ARG) { type = NavType.LongType },
        ),
    ) {
        ReleaseEpisodesPage(
            onPlayClicked = onPlayClicked,
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.activeDownloadsScreen(
    onBackPressed: () -> Unit,
) {
    composable(ACTIVE_DOWNLOADS_ROUTE) {
        ActiveDownloadsPage(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavHostController.navigateToReleaseEpisodes(releaseId: Long) {
    navigate("release_episodes_route/$releaseId")
}

fun NavHostController.navigateToActiveDownloads() {
    navigate(ACTIVE_DOWNLOADS_ROUTE)
}
