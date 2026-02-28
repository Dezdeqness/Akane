package com.dezdeqness.videoplayer.core.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.dezdeqness.videoplayer.player.DesktopVideoPlayer

@Composable
actual fun VideoSurface(engine: VideoPlayerManager, modifier: Modifier) {
    val desktopPlayer = engine.player as DesktopVideoPlayer
    val frame by desktopPlayer.videoFrame.collectAsState()

    Box(modifier = modifier.background(Color.Black)) {
        frame?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}
