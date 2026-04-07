package com.dezdeqness.details.ui.model

data class EpisodesUiModel(
    val id: String,
    val name: String,
    val previewUrl: String,
    val ordinal: Long,
    val episodeUrls: LinkedHashMap<String, String> = linkedMapOf(),
    val downloadStatus: DownloadStatusUi? = null,
    val opening: EpisodeTimingUiModel? = null,
    val ending: EpisodeTimingUiModel? = null,
)

data class EpisodeTimingUiModel(
    val start: Long,
    val end: Long,
)

enum class DownloadStatusUi {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
}
