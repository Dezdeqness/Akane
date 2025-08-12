package com.dezdeqness.videoplayer.core.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import platform.AVFoundation.AVPlayer
import kotlin.time.Duration

@Composable
actual fun rememberVideoPlayerState(
    lifecycleAware: Boolean,
    updatePlaybackStateInterval: Duration
): VideoPlayerState {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val avPlayer = remember { AVPlayer() }
    val videoPlayerState = remember {
        VideoPlayerState(IosVideoPlayer(avPlayer), scope, updatePlaybackStateInterval)
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