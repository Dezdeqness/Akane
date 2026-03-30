package com.dezdeqness.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.dezdeqness.downloads.navigation.downloadsScreen
import com.dezdeqness.feed.navigation.feedScreen
import com.dezdeqness.home.navigation.HOME_ROUTE
import com.dezdeqness.home.navigation.homeScreen
import com.dezdeqness.personal.navigation.personalScreen
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RootNavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    rootControllerNavigateToDetails: (Long) -> Unit,
    activeDownloadsCountFlow: StateFlow<Int>,
    onNavigateToFeed: () -> Unit,
    onNavigateToReleaseEpisodes: (Long) -> Unit,
    onNavigateToActiveDownloads: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        modifier = modifier,
    ) {
        homeScreen(onItemClicked = rootControllerNavigateToDetails)
        feedScreen(rootControllerNavigateToDetails)
        personalScreen(
            onItemClicked = rootControllerNavigateToDetails,
            onEmptyListActionClicked = onNavigateToFeed,
        )
        downloadsScreen(
            onReleaseClicked = onNavigateToReleaseEpisodes,
            activeDownloadsCountFlow = activeDownloadsCountFlow,
            onActiveDownloadsClicked = onNavigateToActiveDownloads,
        )
    }
}
