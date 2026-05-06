package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.details.domain.model.VideoQuality
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import com.dezdeqness.videoplayer.core.player.data.MediaItem
import com.dezdeqness.videoplayer.core.player.data.MediaQuality
import com.dezdeqness.videoplayer.core.player.data.MediaSource
import com.dezdeqness.videoplayer.core.player.data.QualityVariant
import com.dezdeqness.videoplayer.core.player.data.SkipRange
import com.dezdeqness.videoplayer.core.player.feature.installPlatformFeatures
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import io.ktor.http.decodeURLPart
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
    private val uiMapper: VideoPlayerUiMapper,
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

    data class ScreenState(
        val title: String = "",
        val isLoading: Boolean = true,
        val isError: Boolean = false,
    )

    val screenState: StateFlow<ScreenState> =
        if (isDownloadedPlaylist) loadDownloadedPlaylist() else loadStreamingRelease()

    init {
        manager.installPlatformFeatures()

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
                    val items = episodes.toMediaItems()
                    val startIndex = items.indexOfFirst { it.id == initialEpisodeId }.coerceAtLeast(0)
                    manager.setPlaylist(items, startIndex)
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

                val items = downloads.filter { it.filePath != null }.map { item ->
                    MediaItem(
                        id = item.episodeId,
                    title = "${item.episodeOrdinal} эпизод — ${item.episodeName}",
                        source = MediaSource.FilePath(item.filePath!!),
                        previewUrl = item.previewUrl,
                        opening = item.opening?.let { SkipRange(it.start * 1000, it.end * 1000) },
                        ending = item.ending?.let { SkipRange(it.start * 1000, it.end * 1000) },
                    )
                }
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

private fun List<EpisodeUiItem>.toMediaItems(): List<MediaItem> = map { episode ->
    MediaItem(
        id = episode.id,
        title = episode.name.ifEmpty { "${episode.ordinal} эпизод" },
        source = MediaSource.MultiQuality(
            variants = episode.episodeUrls.map { (quality, url) ->
                QualityVariant(
                    quality = quality.toTransformToMediaQuality(),
                    url = url,
                )
            },
        ),
        previewUrl = episode.previewUrl,
        opening = episode.opening?.let { SkipRange(it.start * 1000, it.end * 1000) },
        ending = episode.ending?.let { SkipRange(it.start * 1000, it.end * 1000) },
    )
}

private fun VideoQuality.toTransformToMediaQuality(): MediaQuality {
    return when (this) {
        VideoQuality.q480 -> MediaQuality.q480
        VideoQuality.q720 -> MediaQuality.q720
        VideoQuality.q1080 -> MediaQuality.q1080
    }
}
