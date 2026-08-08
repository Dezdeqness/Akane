package com.dezdeqness.home.domain

import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.views.contract.repository.ViewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.collections.emptyList

class LoadContinueWatchingUseCase(
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val viewsRepository: ViewsRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    operator fun invoke(): Flow<ContinueWatchingEntity?> =
        downloadEpisodeRepository.getAllDownloadsAsFlow()
            .map { downloads -> resolveContinueWatching(downloads) }
            .flowOn(coroutineDispatcherProvider.io())

    private suspend fun resolveContinueWatching(
        downloads: List<DownloadEntity>,
    ): ContinueWatchingEntity? {
        val completed = downloads.filter { it.status == DownloadStatus.COMPLETED }
        if (completed.isEmpty()) return null

        val timecodes = viewsRepository.getTimecodesByRecency().getOrDefault(emptyList())
        val watchedEpisodeIds = timecodes
            .filter { it.isWatched }
            .mapTo(mutableSetOf()) { it.releaseEpisodeId }

        val progressRank = HashMap<String, Int>()
        timecodes
            .filterNot { it.isWatched }
            .forEachIndexed { index, timecode ->
                if (!progressRank.containsKey(timecode.releaseEpisodeId)) {
                    progressRank[timecode.releaseEpisodeId] = index
                }
            }

        val download = completed
            .filterNot { it.episodeId in watchedEpisodeIds }
            .minWithOrNull(
                compareBy<DownloadEntity> { progressRank[it.episodeId] ?: Int.MAX_VALUE }
                    .thenByDescending { it.createdAt },
            )
            ?: return null

        return ContinueWatchingEntity(
            releaseId = download.releaseId,
            episodeId = download.episodeId,
            releaseTitle = download.releaseTitle,
            episodeName = download.episodeName,
            episodeOrdinal = download.episodeOrdinal,
            previewUrl = download.previewUrl,
        )
    }
}
