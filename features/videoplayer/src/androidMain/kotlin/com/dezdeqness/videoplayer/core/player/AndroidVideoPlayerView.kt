package com.dezdeqness.videoplayer.core.player

import android.view.TextureView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun VideoPlayerView(modifier: Modifier, playerState: VideoPlayerState) {
    val context = LocalContext.current

    val playerView = remember { TextureView(context) }

    val exoPlayer = (playerState.getPlayer() as AndroidVideoPlayer)

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.clearVideoTextureView(playerView)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(16 / 9F),
        factory = {
            playerView.apply {
                exoPlayer.setVideoTextureView(playerView)
            }
        }
    )
}
