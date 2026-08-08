package com.dezdeqness.home.domain

import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.downloads.contract.model.DownloadEntity
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity
import com.dezdeqness.views.contract.repository.ViewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class LoadContinueWatchingUseCase(
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val viewsRepository: ViewsRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    operator fun invoke(): Flow<ContinueWatchingEntity?> =
        combine(
            downloadEpisodeRepository.getAllDownloadsAsFlow(),
            viewsRepository.getTimecodesByRecencyAsFlow(),
        ) { downloads, timecodes ->
            resolveContinueWatching(downloads, timecodes)
        }
            .catch { emit(null) }
            .flowOn(coroutineDispatcherProvider.io())

    private fun resolveContinueWatching(
        downloads: List<DownloadEntity>,
        timecodes: List<EpisodeTimecodeEntity>,
    ): ContinueWatchingEntity? {
        val completed = downloads.filter { it.status == DownloadStatus.COMPLETED }
        if (completed.isEmpty()) return null

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
