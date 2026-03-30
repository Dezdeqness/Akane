package com.dezdeqness.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dezdeqness.feed.navigation.FEED_ROUTE
import com.dezdeqness.details.navigation.navigateToDetailsScreen
import com.dezdeqness.downloads.navigation.navigateToActiveDownloads
import com.dezdeqness.downloads.navigation.navigateToReleaseEpisodes
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RootScreen(
    rootController: NavHostController,
) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    val appViewModel: AppViewModel = koinViewModel()
    val activeDownloadsCount by appViewModel.activeDownloadsCount.collectAsState()

    RootNavigationScaffold(
        currentDestination = currentDestination,
        activeDownloadsCount = activeDownloadsCount,
        onTabSelected = { route ->
            navigateToRootTab(
                currentRoute = currentDestination,
                targetRoute = route,
                navController = navController,
            )
        },
    ) { contentModifier ->
        RootNavigationHost(
            modifier = contentModifier,
            navController = navController,
            rootControllerNavigateToDetails = rootController::navigateToDetailsScreen,
            activeDownloadsCountFlow = appViewModel.activeDownloadsCount,
            onNavigateToFeed = {
                navigateToRootTab(
                    currentRoute = currentDestination,
                    targetRoute = FEED_ROUTE,
                    navController = navController,
                )
            },
            onNavigateToReleaseEpisodes = rootController::navigateToReleaseEpisodes,
            onNavigateToActiveDownloads = rootController::navigateToActiveDownloads,
        )
    }
}

fun navigateToRootTab(
    currentRoute: String?,
    targetRoute: String,
    navController: NavHostController,
) {
    if (currentRoute == targetRoute) return

    navController.navigate(targetRoute) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}