package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.details.domain.model.VideoQuality
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.downloads.domain.repository.DownloadEpisodeRepository
import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import com.dezdeqness.videoplayer.core.player.data.MediaItem
import com.dezdeqness.videoplayer.core.player.data.MediaQuality
import com.dezdeqness.videoplayer.core.player.data.MediaSource
import com.dezdeqness.videoplayer.core.player.data.QualityVariant
import com.dezdeqness.videoplayer.core.player.data.SkipRange
import com.dezdeqness.videoplayer.core.player.feature.installPlatformFeatures
import com.dezdeqness.videoplayer.navigation.DOWNLOAD_RELEASE_ID
import com.dezdeqness.videoplayer.navigation.DOWNLOAD_START_EPISODE_ID
import com.dezdeqness.videoplayer.navigation.EPISODE_ID
import com.dezdeqness.videoplayer.navigation.ID
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import io.ktor.http.decodeURLPart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    savedStateHandle: SavedStateHandle,
    private val dispatchers: CoroutineDispatcherProvider,
) : ViewModel() {

    val manager = VideoPlayerManager(player, viewModelScope)

    private val releaseId: Long = savedStateHandle.get<Long>(ID) ?: -1
    private val initialEpisodeId: String = savedStateHandle.get<String>(EPISODE_ID).orEmpty()
    private val downloadReleaseId: Long = savedStateHandle.get<Long>(DOWNLOAD_RELEASE_ID) ?: -1L
    private val downloadStartEpisodeId: String =
        savedStateHandle.get<String>(DOWNLOAD_START_EPISODE_ID).orEmpty().decodeURLPart()

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
                    ScreenState(isLoading = false, isError = true)
                } else {
                    val items = episodes.toMediaItems()
                    val startIdx = items.indexOfFirst { it.id == initialEpisodeId }.coerceAtLeast(0)
                    manager.setPlaylist(items, startIdx)
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
            val downloads = downloadEpisodeRepository.getCompletedByReleaseId(downloadReleaseId)
            if (downloads.isEmpty()) {
                state.value = ScreenState(isLoading = false, isError = true)
                return@launch
            }

            val items = downloads.filter { it.filePath != null }.map { item ->
                MediaItem(
                    id = item.id.toString(),
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
        }
        return state
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
