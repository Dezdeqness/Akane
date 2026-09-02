package com.dezdeqness.videoplayer.core.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.dezdeqness.videoplayer.player.FrameProvidingPlayer

@Composable
actual fun VideoSurface(engine: VideoPlayerManager, modifier: Modifier) {
    val desktopPlayer = engine.player as FrameProvidingPlayer
    val frame by desktopPlayer.videoFrame.collectAsStateOnLifecycle()

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
