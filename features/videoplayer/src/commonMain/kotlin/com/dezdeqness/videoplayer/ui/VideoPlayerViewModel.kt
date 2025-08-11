package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.videoplayer.navigation.EPISODE_ID
import com.dezdeqness.videoplayer.navigation.ID
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class VideoPlayerViewModel(
    private val releaseRepository: ReleaseRepository,
    private val videoPlayerUiMapper: VideoPlayerUiMapper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var releaseId = savedStateHandle.get<Long>(ID) ?: -1
    private var episodeId = savedStateHandle.get<String>(EPISODE_ID).orEmpty()

    private val titleFlow = MutableStateFlow("")
    private val statusFlow = MutableStateFlow(Status.Initial)
    private val currentEpisodeIdFlow = MutableStateFlow(episodeId)
    private val playlistVisibleFlow = MutableStateFlow(false)

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodesFlow: StateFlow<List<EpisodeUiItem>> = loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val result = releaseRepository.getReleaseById(releaseId)

                emit(result)
            }.flowOn(Dispatchers.IO)
        }
        .map { result ->
            if (result.isFailure) {
                statusFlow.tryEmit(Status.Error)
            } else {
                titleFlow.tryEmit(result.getOrNull()?.name.orEmpty())
                statusFlow.tryEmit(Status.Loaded)
            }

            result.getOrNull()
                ?.episodes
                ?.map(videoPlayerUiMapper::map)
                ?.sortedBy { it.ordinal }
                ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val videoPlayerStateFlow: StateFlow<VideoPlayerState> = combine(
        episodesFlow,
        statusFlow,
        titleFlow,
        currentEpisodeIdFlow,
        playlistVisibleFlow,
    ) { episodes, status, title, currentEpisodeId, playlistVisible ->
        VideoPlayerState(
            title = title,
            episodes = episodes,
            status = status,
            currentEpisodeId = currentEpisodeId.ifEmpty { episodes.firstOrNull()?.id.orEmpty() },
            isPlaylistBottomSheetVisible = playlistVisible,
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, VideoPlayerState())

    fun selectEpisode(episodeId: String) {
        currentEpisodeIdFlow.value = episodeId
    }

    fun onPlaylistActionClicked() {
        playlistVisibleFlow.tryEmit(true)
    }

    fun onPlaylistActionClosed() {
        playlistVisibleFlow.tryEmit(false)
    }

    private sealed class LoadEvent() {
        data object Initial : LoadEvent()
    }

    companion object {
        private const val TAG = "VideoPlayerViewModel"
    }
}
