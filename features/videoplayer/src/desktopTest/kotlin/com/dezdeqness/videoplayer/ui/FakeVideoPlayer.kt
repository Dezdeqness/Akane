package com.dezdeqness.videoplayer.ui

import com.dezdeqness.videoplayer.core.player.PlayerEvent
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeVideoPlayer : VideoPlayer {

    private val _events = MutableSharedFlow<PlayerEvent>(replay = 16, extraBufferCapacity = 16)
    override val events: Flow<PlayerEvent> = _events

    fun emit(vararg events: PlayerEvent) {
        events.forEach { _events.tryEmit(it) }
    }

    override fun play() = Unit
    override fun pause() = Unit
    override fun stop() = Unit
    override fun release() = Unit
    override fun seekTo(positionMs: Long) = Unit
    override fun setVolume(volume: Float) = Unit
    override fun setPlaybackSpeed(speed: Float) = Unit
    override fun seekBack() = Unit
    override fun seekForward() = Unit
    override fun setMediaItems(mediaItems: List<String>, startIndex: Int, startPositionMs: Long) = Unit
}
