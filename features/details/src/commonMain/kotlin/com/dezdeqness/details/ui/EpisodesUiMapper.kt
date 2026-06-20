package com.dezdeqness.details.ui

import com.dezdeqness.release.contract.model.EpisodeEntity
import com.dezdeqness.release.contract.model.TimingEntity
import com.dezdeqness.details.ui.model.DownloadStatusUi
import com.dezdeqness.details.ui.model.EpisodeTimingUiModel
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.network.constants.ImageUrlBuilder

class EpisodesUiMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(item: EpisodeEntity, downloads: List<DownloadEntity> = emptyList()) : EpisodesUiModel {
        val download = downloads.find { it.episodeId == item.id }
        return EpisodesUiModel(
            id = item.id,
            name = item.name,
            previewUrl = imageUrlBuilder.build(item.previewUrl),
            ordinal = item.ordinal,
            episodeUrls = LinkedHashMap(item.episodeUrls.map { (quality, url) ->
                quality.nameQuality to url
            }.toMap()),
            downloadStatus = download?.status?.toUi(),
            opening = item.opening?.toUi(),
            ending = item.ending?.toUi(),
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

    private fun TimingEntity.toUi(): EpisodeTimingUiModel {
        return EpisodeTimingUiModel(
            start = start,
            end = end,
        )
    }
}
