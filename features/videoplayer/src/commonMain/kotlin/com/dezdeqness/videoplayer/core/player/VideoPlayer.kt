package com.dezdeqness.videoplayer.core.player

import kotlinx.coroutines.flow.Flow

interface VideoPlayer {
    val events: Flow<PlayerEvent>

    fun play()
    fun pause()
    fun stop()
    fun release()

    fun seekTo(positionMs: Long)
    fun setVolume(volume: Float)
    fun setPlaybackSpeed(speed: Float)

    fun setMediaItems(
        mediaItems: List<String>,
        startIndex: Int,
        startPositionMs: Long,
    )
}
