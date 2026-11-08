package com.dezdeqness.videoplayer.core.player.data

import com.dezdeqness.foundation.utils.toEpisodeOrdinalString

data class MediaItem(
    val id: String,
    val name: String,
    val ordinal: Double,
    val source: MediaSource,
    val previewUrl: String = "",
    val opening: SkipRange? = null,
    val ending: SkipRange? = null,
) {
    val title = if (name.isBlank()) {
        "${ordinal.toEpisodeOrdinalString()} эпизод"
    } else {
        "${ordinal.toEpisodeOrdinalString()} эпизод — $name"
    }
}

data class SkipRange(val startMs: Long, val endMs: Long)
