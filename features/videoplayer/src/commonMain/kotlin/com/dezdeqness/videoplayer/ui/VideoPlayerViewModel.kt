package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.release.contract.repository.ReleaseRepository
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.installPlatformFeatures
import com.dezdeqness.videoplayer.core.player.feature.raw.TimecodeFeature
import com.dezdeqness.videoplayer.core.player.feature.ui.ResumePromptFeature
import com.dezdeqness.videoplayer.core.player.feature.ui.SkipFeature
import com.dezdeqness.views.contract.repository.ViewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideoPlayerViewModel(
    player: VideoPlayer,
    private val releaseRepository: ReleaseRepository,
    private val downloadEpisodeRepository: DownloadEpisodeRepository,
    private val viewsRepository: ViewsRepository,
    private val uiMapper: VideoPlayerUiMapper,
    private val mediaItemMapper: MediaItemMapper,
    private val dispatchers: CoroutineDispatcherProvider,
    private val analytics: AkaneAnalytics,
    private val errorReporter: AkaneErrorReporter,
    private val releaseId: Long,
    private val initialEpisodeId: String,
    private val downloadReleaseId: Long,
    private val downloadStartEpisodeId: String,
) : ViewModel() {

    val manager = VideoPlayerManager(player, viewModelScope)

    private val isDownloadedPlaylist: Boolean = downloadReleaseId > 0

    // TODO: settings page decider
    private val resumeMode: ResumeMode = ResumeMode.PROMPT

    data class ScreenState(
        val title: String = "",
        val isLoading: Boolean = true,
        val isError: Boolean = false,
    )

    val screenState: StateFlow<ScreenState> =
        if (isDownloadedPlaylist) loadDownloadedPlaylist() else loadStreamingRelease()

    init {
        manager.installPlatformFeatures()
        manager.installFeature(
            TimecodeFeature(
                viewsRepository = viewsRepository,
                releaseId = releaseId,
                dispatcher = dispatchers.io(),
            ),
        )
        if (resumeMode == ResumeMode.PROMPT) {
            val resumePromptFeature = ResumePromptFeature(
                viewsRepository = viewsRepository,
                releaseId = releaseId,
            )
            manager.installFeature(resumePromptFeature)
            manager.registry.getFeature<SkipFeature>(FeatureKey.Skip)
                ?.bindSuppression(resumePromptFeature.isPromptVisible)
        }

        viewModelScope.launch {
            manager.currentItem
                .distinctUntilChangedBy { it?.id }
                .collect { item ->
                    item ?: return@collect
                    analytics.trackPlayerStarted(
                        episodeId = item.id,
                        episodeTitle = item.title,
                    )
                }
        }

        viewModelScope.launch {
            manager.completedItems.collect { item ->
                analytics.trackEpisodeFinished(
                    episodeId = item.id,
                    episodeTitle = item.title,
                )
            }
        }

        viewModelScope.launch {
            manager.playerState
                .map { it.error }
                .distinctUntilChanged()
                .collect { message ->
                    if (!message.isNullOrBlank()) {
                        capturePlayerError(message)
                    }
                }
        }
    }

    override fun onCleared() {
        manager.release()
    }

    private suspend fun resolveResumePosition(startEpisodeId: String?): Long {
        if (startEpisodeId == null) return 0L
        return viewsRepository.getReleaseTimecodes(releaseId)
            .getOrNull()
            ?.firstOrNull { it.releaseEpisodeId == startEpisodeId && !it.isWatched }
            ?.timeMs
            ?.coerceAtLeast(0L)
            ?: 0L
    }

    private fun loadStreamingRelease(): StateFlow<ScreenState> =
        flow { emit(releaseRepository.getReleaseById(releaseId)) }
            .flowOn(dispatchers.io())
            .map { result ->
                val episodes = result.getOrNull()
                    ?.episodes
                    ?.map(uiMapper::map)
                    ?.sortedBy { it.ordinal }

                if (episodes == null) {
                    result.exceptionOrNull()?.let { throwable ->
                        if (throwable is CancellationException) throw throwable
                        errorReporter.captureException(
                            throwable = throwable,
                            message = "Video player load failed",
                            tags = mapOf(
                                "feature" to "videoplayer",
                                "layer" to "viewmodel",
                                "operation" to "load_streaming_release",
                            ),
                            extras = mapOf(
                                "release_id" to releaseId.toString(),
                                "download_release_id" to downloadReleaseId.toString(),
                                "initial_episode_id" to initialEpisodeId,
                                "download_start_episode_id" to downloadStartEpisodeId,
                            ),
                        )
                    }
                    ScreenState(isLoading = false, isError = true)
                } else {
                    val items = episodes.map(mediaItemMapper::toMultiQualityMediaItem)
                    val startIndex = items.indexOfFirst { it.id == initialEpisodeId }.coerceAtLeast(0)
                    val startPositionMs = if (resumeMode == ResumeMode.AUTO) {
                        resolveResumePosition(items.getOrNull(startIndex)?.id)
                    } else {
                        0L
                    }
                    manager.setPlaylist(items, startIndex, startPositionMs)
                    ScreenState(
                        title = result.getOrNull()?.name.orEmpty(),
                        isLoading = false,
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, ScreenState())

    private fun loadDownloadedPlaylist(): StateFlow<ScreenState> {
        val state = MutableStateFlow(ScreenState())
        viewModelScope.launch {
            try {
                val downloads = downloadEpisodeRepository.getCompletedByReleaseId(downloadReleaseId)
                if (downloads.isEmpty()) {
                    errorReporter.captureMessage(
                        message = "Downloaded playlist is empty",
                        tags = mapOf(
                            "feature" to "videoplayer",
                            "layer" to "viewmodel",
                            "operation" to "load_downloaded_playlist",
                        ),
                        extras = mapOf(
                            "release_id" to downloadReleaseId.toString(),
                            "start_episode_id" to downloadStartEpisodeId,
                        ),
                    )
                    state.value = ScreenState(isLoading = false, isError = true)
                    return@launch
                }

                val items = downloads.filter { it.filePath != null }.map(mediaItemMapper::toFilePathMediaItem)
                val startIndex = downloads.indexOfFirst { it.episodeId == downloadStartEpisodeId }
                    .coerceAtLeast(0)

                manager.setPlaylist(items, startIndex)
                state.value = ScreenState(
                    title = downloads.firstOrNull()?.releaseTitle.orEmpty(),
                    isLoading = false,
                )
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                errorReporter.captureException(
                    throwable = throwable,
                    message = "Video player load failed",
                    tags = mapOf(
                        "feature" to "videoplayer",
                        "layer" to "viewmodel",
                        "operation" to "load_downloaded_playlist",
                    ),
                    extras = mapOf(
                        "release_id" to releaseId.toString(),
                        "download_release_id" to downloadReleaseId.toString(),
                        "initial_episode_id" to initialEpisodeId,
                        "download_start_episode_id" to downloadStartEpisodeId,
                    ),
                )
                state.value = ScreenState(isLoading = false, isError = true)
            }
        }
        return state
    }

    private fun capturePlayerError(message: String) {
        errorReporter.captureMessage(
            message = "Video player error: $message",
            tags = mapOf(
                "feature" to "videoplayer",
                "layer" to "viewmodel",
                "operation" to "playback",
            ),
            extras = mapOf(
                "release_id" to releaseId.toString(),
                "episode_id" to manager.currentItem.value?.id.orEmpty(),
                "episode_title" to manager.currentItem.value?.title.orEmpty(),
                "quality" to manager.selectedQuality.value.name,
                "is_downloaded_playlist" to isDownloadedPlaylist.toString(),
            ),
        )
    }
}
