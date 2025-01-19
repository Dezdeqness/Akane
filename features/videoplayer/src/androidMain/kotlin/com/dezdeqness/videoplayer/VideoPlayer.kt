package com.dezdeqness.videoplayer

import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.uri.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

@Composable
actual fun VideoPlayerScreen(videoUrl: String) {
    val context = LocalContext.current

    val playerView = remember { TextureView(context) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(
                Uri.parse(videoUrl)
            )
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.clearVideoTextureView(playerView)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize().aspectRatio(16 / 9F),
            factory = { playerView.apply {
                exoPlayer.setVideoTextureView(playerView)
            } }
        )
    }
}
