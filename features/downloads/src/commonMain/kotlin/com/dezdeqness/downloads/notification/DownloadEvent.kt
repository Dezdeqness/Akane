package com.dezdeqness.downloads.notification

import com.dezdeqness.downloads.contract.model.DownloadEntity

sealed interface DownloadEvent {
    val info: DownloadNotificationInfo

    data class Queued(override val info: DownloadNotificationInfo) : DownloadEvent

    data class Started(
        override val info: DownloadNotificationInfo,
        val progress: Float,
    ) : DownloadEvent

    data class Progress(
        override val info: DownloadNotificationInfo,
        val progress: Float,
        val downloadedSegments: Int,
        val totalSegments: Int,
    ) : DownloadEvent

    data class Completed(override val info: DownloadNotificationInfo) : DownloadEvent

    data class Failed(override val info: DownloadNotificationInfo) : DownloadEvent

    data class Paused(override val info: DownloadNotificationInfo) : DownloadEvent

    data class Cancelled(override val info: DownloadNotificationInfo) : DownloadEvent
}

data class DownloadNotificationInfo(
    val downloadId: Long,
    val releaseId: Long,
    val releaseTitle: String,
    val episodeName: String,
    val episodeOrdinal: Long,
) {
    companion object {
        fun from(download: DownloadEntity) = DownloadNotificationInfo(
            downloadId = download.id,
            releaseId = download.releaseId,
            releaseTitle = download.releaseTitle,
            episodeName = download.episodeName,
            episodeOrdinal = download.episodeOrdinal,
        )
    }
}
