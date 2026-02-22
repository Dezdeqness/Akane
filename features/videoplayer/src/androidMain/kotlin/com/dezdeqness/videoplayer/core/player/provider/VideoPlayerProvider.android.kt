package com.dezdeqness.videoplayer.core.player.provider

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.dezdeqness.videoplayer.core.player.AndroidVideoPlayer
import com.dezdeqness.videoplayer.core.player.VideoPlayer

actual class VideoPlayerProvider(private val context: Context) {
    @OptIn(UnstableApi::class)
    actual fun create(): VideoPlayer = AndroidVideoPlayer(context)
}