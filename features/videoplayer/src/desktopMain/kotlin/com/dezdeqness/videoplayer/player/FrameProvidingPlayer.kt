package com.dezdeqness.videoplayer.player

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.StateFlow

interface FrameProvidingPlayer {
    val videoFrame: StateFlow<ImageBitmap?>
}
