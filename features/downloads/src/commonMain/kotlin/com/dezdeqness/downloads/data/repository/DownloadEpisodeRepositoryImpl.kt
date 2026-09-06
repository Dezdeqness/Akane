package com.dezdeqness.downloads.data.repository

import com.dezdeqness.downloads.data.db.DownloadEpisodeDao
import com.dezdeqness.downloads.data.db.SyncDownloadEpisodeDao
import com.dezdeqness.downloads.data.manager.DownloadFileManager
import com.dezdeqness.downloads.data.mapper.DownloadMapper
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DownloadEpisodeRepositoryImpl(
    private val downloadEpisodeDao: DownloadEpisodeDao,
    private val syncDownloadEpisodeDao: SyncDownloadEpisodeDao,
    private val downloadMapper: DownloadMapper,
    private val fileManager: DownloadFileManager,
) : DownloadEpisodeRepository {

    override fun getAllDownloadsAsFlow(): Flow<List<DownloadEntity>> {
        return downloadEpisodeDao.getAllWithProgressAsFlow().map { list ->
            list.map(downloadMapper::toEntity)
        }
    }

    override fun getActiveDownloadsCountAsFlow(): Flow<Int> {
        val activeStatuses = setOf(
            DownloadStatus.QUEUED,
            DownloadStatus.DOWNLOADING,
        )
        return getAllDownloadsAsFlow().map { list ->
            list.count { it.status in activeStatuses }
        }
    }

    override suspend fun getById(id: Long): DownloadEntity? {
        return downloadEpisodeDao.getWithProgressById(id)?.let(downloadMapper::toEntity)
    }

    override suspend fun getByEpisodeAndQuality(episodeId: String, quality: String): DownloadEntity? {
        val episode = downloadEpisodeDao.getByEpisodeAndQuality(episodeId, quality) ?: return null
        return downloadEpisodeDao.getWithProgressById(episode.id)?.let(downloadMapper::toEntity)
    }

    override suspend fun getByEpisodeId(episodeId: String): List<DownloadEntity> {
        return downloadEpisodeDao.getByEpisodeId(episodeId).mapNotNull { episode ->
            downloadEpisodeDao.getWithProgressById(episode.id)?.let(downloadMapper::toEntity)
        }
    }

    override suspend fun insert(entity: DownloadEntity): Long {
        val episodeId = downloadEpisodeDao.insertEpisode(downloadMapper.toEpisodeLocal(entity))
        syncDownloadEpisodeDao.insertProgress(downloadMapper.toProgressLocal(episodeId, entity))
        return episodeId
    }

    override suspend fun getCompletedByReleaseId(releaseId: Long): List<DownloadEntity> {
        return downloadEpisodeDao.getCompletedByReleaseId(releaseId).map(downloadMapper::toEntity)
    }

    override suspend fun getAvailableCompletedByReleaseId(releaseId: Long): List<DownloadEntity> {
        return getCompletedByReleaseId(releaseId)
            .filter { fileManager.isFileAvailable(it.filePath) }
    }

    override fun getCompletedByReleaseIdAsFlow(releaseId: Long): Flow<List<DownloadEntity>> {
        return downloadEpisodeDao.getCompletedByReleaseIdAsFlow(releaseId).map { list ->
            list.map(downloadMapper::toEntity)
        }
    }

    override suspend fun getByStatuses(statuses: List<DownloadStatus>): List<DownloadEntity> {
        return downloadEpisodeDao.getByStatuses(statuses.map { it.name })
            .map(downloadMapper::toEntity)
    }

    override suspend fun deleteRecord(id: Long) {
        downloadEpisodeDao.delete(id)
    }
}
