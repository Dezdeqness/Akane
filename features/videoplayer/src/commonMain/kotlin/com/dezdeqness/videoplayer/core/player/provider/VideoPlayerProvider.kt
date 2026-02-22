package com.dezdeqness.videoplayer.core.player.provider

import com.dezdeqness.videoplayer.core.player.VideoPlayer

expect class VideoPlayerProvider {
    fun create(): VideoPlayer
}
