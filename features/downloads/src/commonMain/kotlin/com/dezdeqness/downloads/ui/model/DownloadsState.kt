package com.dezdeqness.downloads.ui.model

data class ReleaseGroup(
    val releaseId: Long,
    val releaseTitle: String,
    val previewUrl: String,
    val episodes: List<DownloadUiModel>,
    val totalSize: Int,
    val availableCount: Int = totalSize,
)
