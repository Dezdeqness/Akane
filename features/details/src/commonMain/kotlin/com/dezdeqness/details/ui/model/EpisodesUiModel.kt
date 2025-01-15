package com.dezdeqness.details.ui.model

data class EpisodesUiModel(
    val id: String,
    val name: String,
    val previewUrl: String,
    val ordinal: Long,
    val hls720: String?,
)
