package com.dezdeqness.videoplayer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dezdeqness.videoplayer.ui.VideoPlayerPage
import io.ktor.http.decodeURLPart
import io.ktor.http.encodeURLParameter

const val ID = "id"
const val EPISODE_ID = "episodeId"
const val VIDEO_PLAYER_ROUTE = "video_player_route/{$ID}/{$EPISODE_ID}"

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
    ) { entry ->
        val id = entry.savedStateHandle.get<Long>(ID) ?: throw NullPointerException()
        val episodeId = entry.savedStateHandle.get<String>(EPISODE_ID) ?: throw NullPointerException()

        VideoPlayerPage(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavHostController.navigateToVideoPlayerScreen(id: Long, episodeId: String) {
    navigate("video_player_route/$id/$episodeId")
}

class VideoPlayerNavigationControllerImpl : VideoPlayerNavigationController {
    override fun navigateToPlayer(controller: NavHostController, id: Long, episodeId: String) {
        controller.navigateToVideoPlayerScreen(id = id, episodeId = episodeId)
    }
}
