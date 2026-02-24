package com.dezdeqness.videoplayer.ui

import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.dezdeqness.videoplayer.core.player.AndroidVideoPlayer
import com.dezdeqness.videoplayer.core.player.VideoPlayerController

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayerView(modifier: Modifier, controller: VideoPlayerController) {
    val videoPlayer = controller.player as AndroidVideoPlayer
    val exoPlayer = videoPlayer.exoPlayer
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
            }
        }
    )
    val context = LocalContext.current

    val playerView = remember { TextureView(context) }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.clearVideoTextureView(playerView)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            playerView.apply {
                exoPlayer.setVideoTextureView(playerView)
            }
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }
}
