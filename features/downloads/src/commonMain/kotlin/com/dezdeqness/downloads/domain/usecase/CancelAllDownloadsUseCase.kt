package com.dezdeqness.downloads.domain.usecase

import com.dezdeqness.downloads.data.manager.DownloadManager
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.contract.repository.SyncDownloadsEpisodeRepository

class CancelAllDownloadsUseCase(
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncDownloadsRepository: SyncDownloadsEpisodeRepository,
    private val downloadManager: DownloadManager,
) {

    suspend operator fun invoke(releaseId: Long) {
        val activeStatuses = listOf(
            DownloadStatus.QUEUED,
            DownloadStatus.DOWNLOADING,
            DownloadStatus.REMUXING,
            DownloadStatus.PAUSED,
        )
        val activeDownloads = downloadEpisodeRepository.getByStatuses(activeStatuses)
            .filter { it.releaseId == releaseId }
        activeDownloads.forEach { download ->
            downloadManager.cancel(download.id)
            syncDownloadsRepository.updateStatus(download.id, DownloadStatus.CANCELLED)
        }
    }
}
