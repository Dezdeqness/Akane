package com.dezdeqness.downloads.domain.usecase

import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.data.platform.currentTimeMillis
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.model.DownloadTiming
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.contract.repository.SyncDownloadsEpisodeRepository

class EnqueueDownloadUseCase(
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncDownloadsRepository: SyncDownloadsEpisodeRepository,
    private val downloadManager: DownloadManager,
) {

    suspend operator fun invoke(params: Params) {
        val existing = downloadEpisodeRepository.getByEpisodeAndQuality(params.episodeId, params.quality)
        if (existing != null && existing.status.isRetryable) {
            syncDownloadsRepository.updateStatus(existing.id, DownloadStatus.QUEUED)
            downloadManager.enqueue(existing.id)
            return
        }

        val entity = DownloadEntity(
            releaseId = params.releaseId,
            releaseTitle = params.releaseTitle,
            episodeId = params.episodeId,
            episodeName = params.episodeName,
            episodeOrdinal = params.episodeOrdinal,
            quality = params.quality,
            hlsUrl = params.hlsUrl,
            previewUrl = params.previewUrl,
            createdAt = currentTimeMillis(),
            opening = params.opening,
            ending = params.ending,
        )
        val id = downloadEpisodeRepository.insert(entity)
        downloadManager.enqueue(id)
    }

    data class Params(
        val releaseId: Long,
        val releaseTitle: String,
        val episodeId: String,
        val episodeName: String,
        val episodeOrdinal: Long,
        val quality: String,
        val hlsUrl: String,
        val previewUrl: String,
        val opening: DownloadTiming? = null,
        val ending: DownloadTiming? = null,
    )
}
