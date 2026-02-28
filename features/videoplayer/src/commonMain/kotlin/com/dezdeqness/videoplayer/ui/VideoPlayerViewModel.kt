package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.details.domain.model.VideoQuality
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import com.dezdeqness.videoplayer.core.player.data.MediaItem
import com.dezdeqness.videoplayer.core.player.data.MediaQuality
import com.dezdeqness.videoplayer.core.player.data.MediaSource
import com.dezdeqness.videoplayer.core.player.data.QualityVariant
import com.dezdeqness.videoplayer.core.player.data.SkipRange
import com.dezdeqness.videoplayer.core.player.feature.installPlatformFeatures
import com.dezdeqness.videoplayer.navigation.EPISODE_ID
import com.dezdeqness.videoplayer.navigation.ID
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideoPlayerViewModel(
    player: VideoPlayer,
    private val releaseRepository: ReleaseRepository,
    private val uiMapper: VideoPlayerUiMapper,
    savedStateHandle: SavedStateHandle,
    private val dispatchers: CoroutineDispatcherProvider,
) : ViewModel() {

    val manager = VideoPlayerManager(player, viewModelScope)

    private val releaseId: Long = savedStateHandle.get<Long>(ID) ?: -1
    private val initialEpisodeId: String = savedStateHandle.get<String>(EPISODE_ID).orEmpty()

    data class ScreenState(
        val title: String = "",
        val isLoading: Boolean = true,
        val isError: Boolean = false,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState: StateFlow<ScreenState> = flow { emit(Unit) }
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow { emit(releaseRepository.getReleaseById(releaseId)) }
                .flowOn(dispatchers.io())
        }
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
        .stateIn(viewModelScope, SharingStarted.Lazily, ScreenState())

    init {
        manager.installPlatformFeatures()

        viewModelScope.launch { screenState.collect {} }
    }

    override fun onCleared() {
        manager.release()
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
