package com.dezdeqness.details.ui

import com.dezdeqness.details.domain.model.EpisodeEntity
import com.dezdeqness.details.ui.model.DownloadStatusUi
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.downloads.domain.model.DownloadEntity
import com.dezdeqness.downloads.domain.model.DownloadStatus
import com.dezdeqness.network.constants.BaseUrl

class EpisodesUiMapper {

    fun map(item: EpisodeEntity, downloads: List<DownloadEntity> = emptyList()) : EpisodesUiModel {
        val download = downloads.find { it.episodeId == item.id }
        return EpisodesUiModel(
            id = item.id,
            name = item.name,
            previewUrl = if (item.previewUrl.isNotEmpty()) BaseUrl.BASE_URL_IMAGES + item.previewUrl else "",
            ordinal = item.ordinal,
            episodeUrls = LinkedHashMap(item.episodeUrls.map { (quality, url) ->
                quality.nameQuality to url
            }.toMap()),
            downloadStatus = download?.status?.toUi(),
        )
    }

    private fun DownloadStatus.toUi(): DownloadStatusUi? = when (this) {
        DownloadStatus.QUEUED -> DownloadStatusUi.QUEUED
        DownloadStatus.DOWNLOADING, DownloadStatus.REMUXING -> DownloadStatusUi.DOWNLOADING
        DownloadStatus.PAUSED -> DownloadStatusUi.PAUSED
        DownloadStatus.COMPLETED -> DownloadStatusUi.COMPLETED
        DownloadStatus.FAILED -> DownloadStatusUi.FAILED
        DownloadStatus.CANCELLED -> null
    }
}
