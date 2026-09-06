package com.dezdeqness.downloads.ui.model

import com.dezdeqness.downloads.contract.model.DownloadStatus

data class DownloadUiModel(
    val id: Long,
    val releaseId: Long,
    val episodeId: String,
    val episodeName: String,
    val episodeOrdinal: Long,
    val releaseTitle: String,
    val quality: String,
    val progress: Float,
    val status: DownloadStatus,
    val previewUrl: String,
    val filePath: String?,
    val hiddenFromHistory: Boolean = false,
    val isAvailable: Boolean = true,
)
