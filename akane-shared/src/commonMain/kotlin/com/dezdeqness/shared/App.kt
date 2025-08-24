package com.dezdeqness.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dezdeqness.feed.navigation.FEED_ROUTE
import com.dezdeqness.feed.navigation.feedScreen
import com.dezdeqness.designsystem.AkaneTheme
import com.dezdeqness.details.navigation.detailsScreen
import com.dezdeqness.details.navigation.navigateToDetailsScreen
import com.dezdeqness.personal.navigation.personalScreen
import com.dezdeqness.videoplayer.core.videoController
import com.dezdeqness.videoplayer.navigation.videoPlayerScreen

@Composable
fun App() {
    val controller = remember { videoController() }

    AkaneTheme {
        val rootController = rememberNavController()

        NavHost(
            navController = rootController,
            startDestination = "root",
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(route = "root") {
                val navController = rememberNavController()

                val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.background,
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
                        startDestination = FEED_ROUTE,
                        modifier = Modifier.fillMaxSize().padding(padding)
                    ) {
                        feedScreen(rootController::navigateToDetailsScreen)
                        personalScreen(rootController::navigateToDetailsScreen)
                        composable("search") {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Blue))
                        }
                        composable("profile") {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Green))
                        }
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
