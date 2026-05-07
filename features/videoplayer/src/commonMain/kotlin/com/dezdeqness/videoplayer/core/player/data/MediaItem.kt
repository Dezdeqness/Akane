package com.dezdeqness.videoplayer.core.player.data

data class MediaItem(
    val id: String,
    val name: String,
    val ordinal: Long,
    val source: MediaSource,
    val previewUrl: String = "",
    val opening: SkipRange? = null,
    val ending: SkipRange? = null,
) {
    val title = if (name.isBlank()) {
        "$ordinal эпизод"
    } else {
        "$ordinal эпизод — $name"
    }
}

data class SkipRange(val startMs: Long, val endMs: Long)
