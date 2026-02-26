package com.dezdeqness.videoplayer.core.player.data

data class MediaItem(
    val id: String,
    val title: String = "",
    val source: MediaSource,
    val opening: SkipRange? = null,
    val ending: SkipRange? = null,
)

data class SkipRange(val startMs: Long, val endMs: Long)
