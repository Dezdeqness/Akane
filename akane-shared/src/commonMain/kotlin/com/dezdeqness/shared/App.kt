package com.dezdeqness.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dezdeqness.downloads.navigation.activeDownloadsScreen
import com.dezdeqness.downloads.navigation.releaseEpisodesScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dezdeqness.core.ui.views.image.LocalAstImageLoader
import com.dezdeqness.designsystem.AkaneTheme
import com.dezdeqness.designsystem.imageloader.getImageLoader
import com.dezdeqness.details.navigation.detailsScreen
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
                    RootScreen(rootController = rootController)
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
