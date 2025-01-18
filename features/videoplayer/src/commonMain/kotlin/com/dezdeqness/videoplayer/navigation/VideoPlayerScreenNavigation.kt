package com.dezdeqness.videoplayer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dezdeqness.videoplayer.ui.VideoPlayerPage

const val VIDEO_PLAYER_ROUTE = "video_player_route"

fun NavGraphBuilder.videoPlayerScreen(onReleaseClicked: (Long) -> Unit) {
    composable(VIDEO_PLAYER_ROUTE) {
        VideoPlayerPage()
    }
}
