package com.dezdeqness.videoplayer.player

import kotlinx.coroutines.flow.StateFlow

interface VideoPlayer {

    val playbackState: StateFlow<PlaybackState>

    val currentVideoData: StateFlow<VideoData?>

    val currentPosition: StateFlow<Long>

    val currentBufferedDuration: StateFlow<Long>

    fun play()

    fun pause()

    fun togglePause()

    fun seekTo(timeStamp: Long)

    fun getCurrentPlaybackState(): PlaybackState
}

enum class PlaybackState {
    Ready,
    Paused,
    Playing,
    Loading,
    Buffering,
    Finished,
    Error
}
