package com.dezdeqness.videoplayer.ui.model

data class EpisodeUiItem(
    val id: String,
    val name: String,
    val ordinal: Long,
    val hls480: String?,
    val hls720: String?,
    val hls1080: String?,
    val nameEnglish: String?,
)
