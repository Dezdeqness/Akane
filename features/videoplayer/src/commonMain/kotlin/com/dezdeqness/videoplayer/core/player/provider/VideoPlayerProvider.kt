package com.dezdeqness.videoplayer.core.player.provider

import com.dezdeqness.videoplayer.core.player.api.VideoPlayer

expect class VideoPlayerProvider {
    fun create(): VideoPlayer
}
