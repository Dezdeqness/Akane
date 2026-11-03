package com.dezdeqness.videoplayer.core.player.provider

import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import com.dezdeqness.videoplayer.core.player.IosVideoPlayer

actual class VideoPlayerProvider {
    actual fun createPreview(): VideoPlayer = create()

    actual fun create(): VideoPlayer = IosVideoPlayer()
}
