package com.dezdeqness.videoplayer.core.player

sealed class PlayerEvent {
    data class IsPlaying(val value: Boolean) : PlayerEvent()
    data class IsBuffering(val value: Boolean) : PlayerEvent()
    data class DurationChanged(val durationMs: Long) : PlayerEvent()
    data class PositionChanged(val positionMs: Long) : PlayerEvent()
    data class BufferedChanged(val bufferedMs: Long) : PlayerEvent()
    object PlaybackEnded : PlayerEvent()
    data class Error(val message: String) : PlayerEvent()
}
