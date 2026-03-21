package com.dezdeqness.downloads.ui.model

import com.dezdeqness.downloads.domain.model.DownloadStatus

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
)
