package com.dezdeqness.videoplayer.core.player.provider

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import com.dezdeqness.videoplayer.core.player.AndroidVideoPlayer
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer

actual class VideoPlayerProvider(
    private val context: Context,
    private val downloadCache: Cache?,
) {
    @OptIn(UnstableApi::class)
    actual fun create(): VideoPlayer = AndroidVideoPlayer(context, downloadCache)
}
