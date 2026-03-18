package com.dezdeqness.downloads.data.mapper

import com.dezdeqness.downloads.data.db.DownloadEpisodeLocal
import com.dezdeqness.downloads.data.db.DownloadProgressLocal
import com.dezdeqness.downloads.data.db.DownloadWithProgress
import com.dezdeqness.downloads.domain.model.DownloadEntity
import com.dezdeqness.downloads.domain.model.DownloadStatus

class DownloadMapper {

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
            filePath = episode.filePath,
            status = progress?.let { DownloadStatus.valueOf(it.status) } ?: DownloadStatus.QUEUED,
            progress = progress?.progress ?: 0f,
            totalSegments = progress?.totalSegments ?: 0,
            downloadedSegments = progress?.downloadedSegments ?: 0,
            createdAt = episode.createdAt,
            previewUrl = episode.previewUrl,
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
    )

    fun toProgressLocal(episodeDownloadId: Long, entity: DownloadEntity) = DownloadProgressLocal(
        episodeDownloadId = episodeDownloadId,
        status = entity.status.name,
        progress = entity.progress,
        totalSegments = entity.totalSegments,
        downloadedSegments = entity.downloadedSegments,
    )
}
