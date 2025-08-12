@file:OptIn(UnstableApi::class)

package com.dezdeqness.videoplayer.core.player

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlin.time.Duration

@Composable
actual fun rememberVideoPlayerState(
    lifecycleAware: Boolean,
    updatePlaybackStateInterval: Duration
): VideoPlayerState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = remember {
        ExoPlayer
            .Builder(context)
            .setSeekForwardIncrementMs(10000L)
            .setSeekBackIncrementMs(10000L)
            .build()
    }
    val videoPlayerState = remember {
        VideoPlayerState(AndroidVideoPlayer(exoPlayer), scope, updatePlaybackStateInterval)
    }

    if (lifecycleAware) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> videoPlayerState.play()
                    Lifecycle.Event.ON_PAUSE -> videoPlayerState.pause()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                videoPlayerState.release()
            }
        }
    }

    return videoPlayerState
}
