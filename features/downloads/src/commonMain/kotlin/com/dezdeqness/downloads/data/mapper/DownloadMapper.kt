package com.dezdeqness.downloads.data.mapper

import com.dezdeqness.downloads.data.db.DownloadEpisodeLocal
import com.dezdeqness.downloads.data.db.DownloadProgressLocal
import com.dezdeqness.downloads.data.db.DownloadWithProgress
import com.dezdeqness.downloads.data.manager.DownloadFileManager
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.model.DownloadTiming

class DownloadMapper(
    private val fileManager: DownloadFileManager,
) {

    fun toEntity(withProgress: DownloadWithProgress): DownloadEntity {
        val episode = withProgress.episode
        val progress = withProgress.progress
        return DownloadEntity(
            id = episode.id,
            releaseId = episode.releaseId,
            releaseTitle = episode.releaseTitle,
            episodeId = episode.episodeId,
            episodeName = episode.episodeName,
            episodeOrdinal = episode.episodeOrdinal,
            quality = episode.quality,
            hlsUrl = episode.hlsUrl,
            filePath = episode.filePath?.let { fileManager.resolveFilePath(it) },
            status = progress?.let { DownloadStatus.valueOf(it.status) } ?: DownloadStatus.QUEUED,
            progress = progress?.progress ?: 0f,
            totalSegments = progress?.totalSegments ?: 0,
            downloadedSegments = progress?.downloadedSegments ?: 0,
            createdAt = episode.createdAt,
            previewUrl = episode.previewUrl,
            hiddenFromHistory = progress?.hiddenFromHistory ?: false,
            opening = toDownloadTiming(episode.openingStart, episode.openingEnd),
            ending = toDownloadTiming(episode.endingStart, episode.endingEnd),
        )
    }

    fun toEpisodeLocal(entity: DownloadEntity) = DownloadEpisodeLocal(
        id = entity.id,
        releaseId = entity.releaseId,
        releaseTitle = entity.releaseTitle,
        episodeId = entity.episodeId,
        episodeName = entity.episodeName,
        episodeOrdinal = entity.episodeOrdinal,
        quality = entity.quality,
        hlsUrl = entity.hlsUrl,
        filePath = entity.filePath,
        createdAt = entity.createdAt,
        previewUrl = entity.previewUrl,
        openingStart = entity.opening?.start,
        openingEnd = entity.opening?.end,
        endingStart = entity.ending?.start,
        endingEnd = entity.ending?.end,
    )

    fun toProgressLocal(episodeDownloadId: Long, entity: DownloadEntity) = DownloadProgressLocal(
        episodeDownloadId = episodeDownloadId,
        status = entity.status.name,
        progress = entity.progress,
        totalSegments = entity.totalSegments,
        downloadedSegments = entity.downloadedSegments,
    )

    private fun toDownloadTiming(startTime: Long?, endTime: Long?): DownloadTiming? {
        val start = startTime ?: return null
        val end = endTime ?: return null
        return DownloadTiming(start = start, end = end)
    }
}
