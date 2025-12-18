package com.dezdeqness.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.LocalAstImageLoader
import com.dezdeqness.designsystem.AkaneTheme
import com.dezdeqness.designsystem.imageloader.getImageLoader
import com.dezdeqness.details.navigation.detailsScreen
import com.dezdeqness.details.navigation.navigateToDetailsScreen
import com.dezdeqness.feed.navigation.FEED_ROUTE
import com.dezdeqness.feed.navigation.feedScreen
import com.dezdeqness.home.navigation.HOME_ROUTE
import com.dezdeqness.home.navigation.homeScreen
import com.dezdeqness.personal.navigation.personalScreen
import com.dezdeqness.videoplayer.core.videoController
import com.dezdeqness.videoplayer.navigation.videoPlayerScreen

@Composable
fun App() {
    val controller = remember { videoController() }

    CompositionLocalProvider(
        LocalAstImageLoader provides getImageLoader()
    ) {
        AkaneTheme {
            val rootController = rememberNavController()

            NavHost(
                navController = rootController,
                startDestination = "root",
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(route = "root") {
                    val navController = rememberNavController()

                    val currentDestination =
                        navController.currentBackStackEntryAsState().value?.destination?.route

                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = AppTheme.colors.background,
                                tonalElevation = 0.dp,
                            ) {
                                AkaneBottomTabModel.entries.forEach { item ->
                                    NavigationBarItem(
                                        selected = currentDestination == item.route,
                                        onClick = {
                                            if (currentDestination != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentDestination == item.route) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = null,
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = HOME_ROUTE,
                            modifier = Modifier.fillMaxSize().padding(padding)
                        ) {
                            homeScreen(rootController::navigateToDetailsScreen)
                            feedScreen(rootController::navigateToDetailsScreen)
                            personalScreen(
                                onItemClicked = rootController::navigateToDetailsScreen,
                                onEmptyListActionClicked = {
                                    navController.navigate(FEED_ROUTE) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }

                detailsScreen(
                    onBackPressed = rootController::navigateUp,
                    onEpisodeClick = { id, episodeId ->
                        controller.navigateToPlayer(rootController, id, episodeId)
                    },
                )
                videoPlayerScreen(onBackPressed = rootController::navigateUp)
            }

        }
    }
}
