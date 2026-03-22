package com.dezdeqness.downloads.domain.usecase

import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.domain.model.DownloadStatus
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.domain.repository.SyncDownloadsEpisodeRepository

class CancelDownloadUseCase(
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncDownloadsRepository: SyncDownloadsEpisodeRepository,
    private val downloadManager: DownloadManager,
) {

    suspend operator fun invoke(episodeId: String) {
        val downloads = downloadEpisodeRepository.getByEpisodeId(episodeId)
        downloads.forEach { download ->
            downloadManager.cancel(download.id)
            syncDownloadsRepository.updateStatus(download.id, DownloadStatus.CANCELLED)
        }
    }
}
