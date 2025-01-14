package com.dezdeqness.details.ui.model

data class ReleaseDetailsUiModel(
    val id: Long,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val genres: List<String>,
    val episodesTotal: Long,
    val episodes: List<EpisodesUiModel>,
)
