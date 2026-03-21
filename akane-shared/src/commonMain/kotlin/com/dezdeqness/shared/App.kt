package com.dezdeqness.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.downloads.navigation.activeDownloadsScreen
import com.dezdeqness.downloads.navigation.downloadsScreen
import com.dezdeqness.downloads.navigation.navigateToActiveDownloads
import com.dezdeqness.downloads.navigation.navigateToReleaseEpisodes
import com.dezdeqness.downloads.navigation.releaseEpisodesScreen
import org.koin.compose.viewmodel.koinViewModel
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
import com.dezdeqness.videoplayer.navigation.downloadedPlaylistScreen
import com.dezdeqness.videoplayer.navigation.videoController
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

                    val appViewModel: AppViewModel = koinViewModel()
                    val activeDownloadsCount by appViewModel.activeDownloadsCount.collectAsState()

                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = AppTheme.colors.background,
                                tonalElevation = 0.dp,
                            ) {
                                AkaneBottomTabModel.entries.forEach { item ->
                                    val isSelected = currentDestination == item.route
                                    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
                                    val showBadge = item == AkaneBottomTabModel.DOWNLOADS
                                            && !isSelected
                                            && activeDownloadsCount > 0

                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            if (!isSelected) {
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
                                            if (showBadge) {
                                                BadgedBox(
                                                    badge = {
                                                        Badge { Text(activeDownloadsCount.toString()) }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                )
                                            }
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
                            homeScreen(onItemClicked = rootController::navigateToDetailsScreen)
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
                            downloadsScreen(
                                onReleaseClicked = { releaseId ->
                                    rootController.navigateToReleaseEpisodes(releaseId)
                                },
                                activeDownloadsCountFlow = appViewModel.activeDownloadsCount,
                                onActiveDownloadsClicked = {
                                    rootController.navigateToActiveDownloads()
                                },
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
                downloadedPlaylistScreen(onBackPressed = rootController::navigateUp)
                releaseEpisodesScreen(
                    onBackPressed = rootController::navigateUp,
                    onPlayClicked = { releaseId, episodeId ->
                        controller.navigateToDownloadedPlaylist(
                            controller = rootController,
                            releaseId = releaseId,
                            startEpisodeId = episodeId,
                        )
                    },
                )
                activeDownloadsScreen(
                    onBackPressed = rootController::navigateUp,
                )
            }

        }
    }
}
