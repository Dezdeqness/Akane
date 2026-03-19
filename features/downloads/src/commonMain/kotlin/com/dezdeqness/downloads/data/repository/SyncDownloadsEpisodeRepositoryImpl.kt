package com.dezdeqness.downloads.data.repository

import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.data.db.SyncDownloadEpisodeDao
import com.dezdeqness.downloads.domain.model.DownloadStatus
import com.dezdeqness.downloads.domain.repository.SyncDownloadsEpisodeRepository

class SyncDownloadsEpisodeRepositoryImpl(
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val syncDownloadEpisodeDao: SyncDownloadEpisodeDao,
) : SyncDownloadsEpisodeRepository {

    override suspend fun updateStatus(id: Long, status: DownloadStatus) {
        syncDownloadEpisodeDao.updateStatus(id, status.name)
    }

    override suspend fun updateProgress(id: Long, progress: Float, downloadedSegments: Int) {
        syncDownloadEpisodeDao.updateProgress(id, progress, downloadedSegments)
    }

    override suspend fun updateTotalSegments(id: Long, totalSegments: Int) {
        syncDownloadEpisodeDao.updateTotalSegments(id, totalSegments)
    }

    override suspend fun updateFilePath(id: Long, filePath: String) {
        syncDownloadEpisodeDao.updateFilePath(id, filePath)
    }

    override suspend fun markCompleted(id: Long) {
        syncDownloadEpisodeDao.markCompleted(id)
    }

    override suspend fun delete(id: Long) {
        downloadEpisodeRepository.deleteRecord(id)
    }
}
