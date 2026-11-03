package com.dezdeqness.videoplayer.core.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun CompositedVideoSurface(engine: VideoPlayerManager, modifier: Modifier) =
    VideoSurface(engine = engine, modifier = modifier)

actual val compositedVideoSurfaceFollowsTransforms: Boolean = false
