package com.dezdeqness.videoplayer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dezdeqness.videoplayer.ui.VideoPlayerPage
import io.ktor.http.decodeURLPart
import io.ktor.http.encodeURLParameter

const val EPISODE_URL = "episodeUrl"
const val VIDEO_PLAYER_ROUTE = "video_player_route/{$EPISODE_URL}"

fun NavGraphBuilder.videoPlayerScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        VIDEO_PLAYER_ROUTE,
        arguments = listOf(navArgument(EPISODE_URL) { type = NavType.StringType }),
    ) { entry ->
        val url = entry.arguments?.getString(EPISODE_URL) ?: throw NullPointerException()
        val decodedUrl = url.decodeURLPart()
        VideoPlayerPage(
            url = decodedUrl,
            onBackPressed = onBackPressed,
        )
    }
}

fun NavHostController.navigateToVideoPlayerScreen(videoUrl: String) {
    val encodedUrl = videoUrl.encodeURLParameter()
    navigate("video_player_route/$encodedUrl")
}

class VideoPlayerNavigationControllerImpl : VideoPlayerNavigationController {
    override fun navigateToPlayer(controller: NavHostController, url: String) {
        controller.navigateToVideoPlayerScreen(url)
    }
}
