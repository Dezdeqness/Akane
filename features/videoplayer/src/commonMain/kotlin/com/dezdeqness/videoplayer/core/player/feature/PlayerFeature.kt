package com.dezdeqness.videoplayer.core.player.feature

import androidx.compose.ui.Modifier
import com.dezdeqness.videoplayer.core.player.VideoPlayerController

interface PlayerFeature {
    fun install(controller: VideoPlayerController)
    fun modifier(): Modifier = Modifier
    fun dispose()
}
