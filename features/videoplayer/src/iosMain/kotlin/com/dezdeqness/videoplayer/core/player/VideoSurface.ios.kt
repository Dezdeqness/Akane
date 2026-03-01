package com.dezdeqness.videoplayer.core.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoSurface(engine: VideoPlayerManager, modifier: Modifier) {
    val iosPlayer = engine.player as IosVideoPlayer

    UIKitView(
        modifier = modifier,
        factory = { iosPlayer.makePlayerView() },
    )
}
