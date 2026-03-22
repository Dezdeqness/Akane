package com.dezdeqness.details.ui.model

data class EpisodesUiModel(
    val id: String,
    val name: String,
    val previewUrl: String,
    val ordinal: Long,
    val episodeUrls: LinkedHashMap<String, String> = linkedMapOf(),
    val downloadStatus: DownloadStatusUi? = null,
)

enum class DownloadStatusUi {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
}
