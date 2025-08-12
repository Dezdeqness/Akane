package com.dezdeqness.videoplayer.core.player

interface VideoPlayerListener {
    fun onIsPlayingChanged(isPlaying: Boolean)
    fun onIsLoadingChanged(isLoading: Boolean)
    fun isBuffering(buffering: Boolean)
}
