package com.dezdeqness.videoplayer.core.player

import android.view.Gravity
import android.view.TextureView
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout


@OptIn(UnstableApi::class)
@Composable
actual fun CompositedVideoSurface(engine: VideoPlayerManager, modifier: Modifier) {
    val exoPlayer = (engine.player as AndroidVideoPlayer).exoPlayer
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val textureView = remember(context) { TextureView(context) }
    val aspectLayout = remember(context) {
        AspectRatioFrameLayout(context).apply {
            addView(textureView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
    }

    var hasFrame by remember(exoPlayer) { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (hasFrame) 1f else 0f,
        animationSpec = tween(durationMillis = if (hasFrame) FADE_IN_MS else 0),
        label = "videoFadeIn",
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> if (engine.playerState.value.isPlaying) exoPlayer.play()
                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(exoPlayer, textureView) {
        fun applyVideoSize(size: VideoSize) {
            if (size.width == 0 || size.height == 0) return
            aspectLayout.setAspectRatio(size.width * size.pixelWidthHeightRatio / size.height)
        }

        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) = applyVideoSize(videoSize)

            override fun onRenderedFirstFrame() {
                hasFrame = true
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                hasFrame = false
            }
        }
        exoPlayer.setVideoTextureView(textureView)
        exoPlayer.addListener(listener)
        applyVideoSize(exoPlayer.videoSize)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.clearVideoTextureView(textureView)
        }
    }

    AndroidView(
        modifier = modifier.graphicsLayer { this.alpha = alpha },
        factory = { context ->
            FrameLayout(context).apply {
                (aspectLayout.parent as? FrameLayout)?.removeView(aspectLayout)
                addView(aspectLayout, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER))
            }
        },
    )
}

actual val compositedVideoSurfaceFollowsTransforms: Boolean = true

private const val MATCH_PARENT = FrameLayout.LayoutParams.MATCH_PARENT
private const val FADE_IN_MS = 280
