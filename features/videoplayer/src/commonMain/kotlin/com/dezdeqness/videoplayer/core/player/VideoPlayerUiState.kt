package com.dezdeqness.videoplayer.core.player

data class VideoPlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val duration: Long = 0L,
    val position: Long = 0L,
    val buffered: Long = 0L,
    val error: String? = null,
) {
    val progress: Float
        get() = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f
}
