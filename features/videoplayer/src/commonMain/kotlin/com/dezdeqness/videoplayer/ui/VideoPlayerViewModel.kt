package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.videoplayer.navigation.EPISODE_ID
import com.dezdeqness.videoplayer.navigation.ID
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    private var releaseId = savedStateHandle.get<Long>(ID) ?: -1
    private var episodeId = savedStateHandle.get<String>(EPISODE_ID).orEmpty()

    private val currentEpisodeIdFlow = MutableStateFlow(episodeId)
    private val playlistVisibleFlow = MutableStateFlow(false)
    private val videoSpeedFlow = MutableStateFlow(VideoSpeedData())
    private val qualityDataFlow = MutableStateFlow(QualityData())

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val videoDataFlow: StateFlow<VideoData> = loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val result = releaseRepository.getReleaseById(releaseId)

                emit(result)
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .map { result ->
            val list = result.getOrNull()
                ?.episodes
                ?.map(videoPlayerUiMapper::map)
                ?.sortedBy { it.ordinal }
                ?: emptyList()
            VideoData(
                title = result.getOrNull()?.name.orEmpty(),
                episodes = list,
                status = if (result.isFailure) Status.Error else Status.Loaded
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, VideoData(status = Status.Loading))

    val videoPlayerStateFlow: StateFlow<VideoPlayerState> = combine(
        videoDataFlow,
        currentEpisodeIdFlow,
        playlistVisibleFlow,
        videoSpeedFlow,
        qualityDataFlow,
    ) { videoData, currentEpisodeId, playlistVisible, videoSpeedData, qualityData ->

        VideoPlayerState(
            videoData = videoData,
            currentEpisodeId = currentEpisodeId.ifEmpty { videoData.episodes.firstOrNull()?.id.orEmpty() },
            isPlaylistBottomSheetVisible = playlistVisible,
            videoSpeedData = videoSpeedData,
            qualityData = qualityData,
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, VideoPlayerState())

    fun onSelectEpisode(episodeId: String) {
        currentEpisodeIdFlow.value = episodeId
    }

    fun onPlaylistActionClicked() {
        playlistVisibleFlow.tryEmit(true)
    }

    fun onPlaylistActionClosed() {
        playlistVisibleFlow.tryEmit(false)
    }

    fun onVideoSpeedActionClicked() {
        videoSpeedFlow.tryEmit(videoSpeedFlow.value.copy(isVideoSpeedDropdownVisible = true))
    }

    fun onVideoSpeedActionClosed() {
        videoSpeedFlow.tryEmit(videoSpeedFlow.value.copy(isVideoSpeedDropdownVisible = false))
    }

    fun onVideoSpeedSelect(videoSpeed: VideoSpeed) {
        videoSpeedFlow.tryEmit(videoSpeedFlow.value.copy(videoSpeed = videoSpeed))
    }

    fun onQualityActionClicked() {
        qualityDataFlow.tryEmit(qualityDataFlow.value.copy(isQualityDropdownVisible = true))
    }

    fun onQualityActionClosed() {
        qualityDataFlow.tryEmit(qualityDataFlow.value.copy(isQualityDropdownVisible = false))
    }

    fun onVideoQualitySelect(videoQuality: VideoQuality) {
        qualityDataFlow.tryEmit(qualityDataFlow.value.copy(currentVideoQuality = videoQuality))
    }

    private sealed class LoadEvent() {
        data object Initial : LoadEvent()
    }

    companion object {
        private const val TAG = "VideoPlayerViewModel"
    }
}
