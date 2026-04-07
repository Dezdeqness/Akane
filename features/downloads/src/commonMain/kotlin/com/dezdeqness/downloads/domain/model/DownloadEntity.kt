package com.dezdeqness.downloads.domain.model

data class DownloadEntity(
    val id: Long = 0,
    val releaseId: Long,
    val releaseTitle: String,
    val episodeId: String,
    val episodeName: String,
    val episodeOrdinal: Long,
    val quality: String,
    val hlsUrl: String,
    val filePath: String? = null,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val progress: Float = 0f,
    val totalSegments: Int = 0,
    val downloadedSegments: Int = 0,
    val createdAt: Long = 0,
    val previewUrl: String = "",
    val hiddenFromHistory: Boolean = false,
    val opening: DownloadTiming? = null,
    val ending: DownloadTiming? = null,
)

data class DownloadTiming(
    val start: Long,
    val end: Long,
)
