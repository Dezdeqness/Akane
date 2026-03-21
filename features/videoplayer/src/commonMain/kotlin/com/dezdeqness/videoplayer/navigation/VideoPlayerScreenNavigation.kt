package com.dezdeqness.videoplayer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dezdeqness.videoplayer.ui.VideoPlayerPage
import io.ktor.http.encodeURLParameter

const val ID = "id"
const val EPISODE_ID = "episodeId"
const val VIDEO_PLAYER_ROUTE = "video_player_route/{$ID}/{$EPISODE_ID}"

const val DOWNLOAD_RELEASE_ID = "downloadReleaseId"
const val DOWNLOAD_START_EPISODE_ID = "downloadStartEpisodeId"
const val DOWNLOADED_PLAYLIST_ROUTE = "downloaded_playlist_route/{$DOWNLOAD_RELEASE_ID}/{$DOWNLOAD_START_EPISODE_ID}"

fun NavGraphBuilder.videoPlayerScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = VIDEO_PLAYER_ROUTE,
        arguments = listOf(
            navArgument(ID) {
                type = NavType.LongType
                nullable = false
            },
            navArgument(EPISODE_ID) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) { _ ->
        VideoPlayerPage(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavHostController.navigateToVideoPlayerScreen(id: Long, episodeId: String) {
    navigate("video_player_route/$id/$episodeId")
}

fun NavGraphBuilder.downloadedPlaylistScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = DOWNLOADED_PLAYLIST_ROUTE,
        arguments = listOf(
            navArgument(DOWNLOAD_RELEASE_ID) {
                type = NavType.LongType
                nullable = false
            },
            navArgument(DOWNLOAD_START_EPISODE_ID) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        VideoPlayerPage(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavHostController.navigateToDownloadedPlaylist(releaseId: Long, startEpisodeId: String) {
    val encodedEpisodeId = startEpisodeId.encodeURLParameter()
    navigate("downloaded_playlist_route/$releaseId/$encodedEpisodeId")
}

class VideoPlayerNavigationControllerImpl : VideoPlayerNavigationController {
    override fun navigateToPlayer(controller: NavHostController, id: Long, episodeId: String) {
        controller.navigateToVideoPlayerScreen(id = id, episodeId = episodeId)
    }

    override fun navigateToDownloadedPlaylist(controller: NavHostController, releaseId: Long, startEpisodeId: String) {
        controller.navigateToDownloadedPlaylist(releaseId = releaseId, startEpisodeId = startEpisodeId)
    }
}
