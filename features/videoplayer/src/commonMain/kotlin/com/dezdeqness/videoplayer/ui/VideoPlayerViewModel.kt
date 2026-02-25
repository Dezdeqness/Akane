package com.dezdeqness.videoplayer.ui

import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.details.domain.model.VideoQuality
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.videoplayer.core.player.VideoPlayer
import com.dezdeqness.videoplayer.core.player.VideoPlayerController
import com.dezdeqness.videoplayer.core.player.feature.FeatureManager
import com.dezdeqness.videoplayer.core.player.feature.platformFeatures
import com.dezdeqness.videoplayer.navigation.EPISODE_ID
import com.dezdeqness.videoplayer.navigation.ID
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
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
    private val videoPlayerUiMapper: VideoPlayerUiMapper,
    savedStateHandle: SavedStateHandle,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    val controller = VideoPlayerController(player, viewModelScope)
    val playerState = controller.state
    val overlay = controller.overlay

    private val features = FeatureManager(platformFeatures(), controller)

    val featuresModifier: Modifier
        get() = features.combinedModifier()

    private val releaseId: Long = savedStateHandle.get<Long>(ID) ?: -1
    private val initialEpisodeId: String = savedStateHandle.get<String>(EPISODE_ID).orEmpty()

    private val selectedEpisodeId = MutableStateFlow(initialEpisodeId)
    private val isPlaylistVisible = MutableStateFlow(false)
    private val reloadEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val videoSpeedFlow = MutableStateFlow(VideoSpeedData())
    private val qualityDataFlow = MutableStateFlow(QualityData())

    private val aspectRatioModeFlow = MutableStateFlow(AspectRatioMode.Fit16_9)
    val aspectRatioMode: StateFlow<AspectRatioMode> =
        aspectRatioModeFlow.stateIn(viewModelScope, SharingStarted.Eagerly, aspectRatioModeFlow.value)

    @OptIn(ExperimentalCoroutinesApi::class)
    val videoData: StateFlow<VideoData> = reloadEvents
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow { emit(releaseRepository.getReleaseById(releaseId)) }
                .flowOn(coroutineDispatcherProvider.io())
        }
        .map { result ->
            val episodes = result.getOrNull()
                ?.episodes
                ?.map(videoPlayerUiMapper::map)
                ?.sortedBy { it.ordinal }
                ?: emptyList()

            VideoData(
                title = result.getOrNull()?.name.orEmpty(),
                episodes = episodes,
                status = if (result.isFailure) Status.Error else Status.Loaded
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, VideoData(status = Status.Loading))

    data class ScreenState(
        val videoData: VideoData = VideoData(status = Status.Loading),
        val currentEpisodeId: String = "",
        val isPlaylistVisible: Boolean = false,
        val videoSpeedData: VideoSpeedData = VideoSpeedData(),
        val qualityData: QualityData = QualityData(),
    )

    val screenState: StateFlow<ScreenState> = combine(
        videoData,
        selectedEpisodeId,
        isPlaylistVisible,
        videoSpeedFlow,
        qualityDataFlow,
    ) { data, epId, playlistVisible, speedData, qualityData ->
        val resolvedId = epId.ifEmpty { data.episodes.firstOrNull()?.id.orEmpty() }
        ScreenState(
            videoData = data,
            currentEpisodeId = resolvedId,
            isPlaylistVisible = playlistVisible,
            videoSpeedData = speedData,
            qualityData = qualityData,
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, ScreenState())

    init {
        features.installAll()

        viewModelScope.launch {
            videoSpeedFlow
                .map { it.videoSpeed.speed }
                .distinctUntilChanged()
                .collect { speed ->
                    controller.setSpeed(speed)
                }
        }

        viewModelScope.launch {
            combine(videoData, selectedEpisodeId, qualityDataFlow) { data, epId, quality ->
                val resolvedId = epId.ifEmpty { data.episodes.firstOrNull()?.id.orEmpty() }

                val urls = data.episodes
                    .mapNotNull { it.urlByQualityOrNull(quality.currentVideoQuality) }

                val startIndex = data.episodes.indexOfFirst { it.id == resolvedId }.coerceAtLeast(0)

                Triple(urls, startIndex, resolvedId)
            }
                .distinctUntilChangedBy { (urls, startIndex, _) -> urls to startIndex }
                .collect { (urls, startIndex, _) ->
                    if (urls.isEmpty()) return@collect

                    val currentPos = controller.state.value.position

                    controller.player.setMediaItems(
                        mediaItems = urls,
                        startIndex = startIndex,
                        startPositionMs = currentPos
                    )
                }
        }
    }

    fun reload() {
        reloadEvents.tryEmit(Unit)
    }

    fun selectEpisode(id: String) {
        selectedEpisodeId.value = id
        isPlaylistVisible.value = false
    }

    fun openPlaylist() {
        isPlaylistVisible.value = true
    }

    fun closePlaylist() {
        isPlaylistVisible.value = false
    }

    fun seekForward() = controller.seekForward()
    fun seekBack() = controller.seekBack()
    fun play() = controller.play()
    fun pause() = controller.pause()
    fun seekTo(ms: Long) = controller.seekTo(ms)

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

    fun onAspectRatioClick() {
        aspectRatioModeFlow.value = when (aspectRatioModeFlow.value) {
            AspectRatioMode.Fit16_9 -> AspectRatioMode.Fit4_3
            AspectRatioMode.Fit4_3 -> AspectRatioMode.Fill
            AspectRatioMode.Fill -> AspectRatioMode.Fit16_9
        }
    }


    override fun onCleared() {
        features.disposeAll()
        controller.release()
    }
}


private fun EpisodeUiItem.urlByQualityOrNull(quality: VideoQuality): String? = episodeUrls[quality] ?: bestUrlOrNull()

private fun EpisodeUiItem.bestUrlOrNull(): String? =
    episodeUrls.toList().lastOrNull()?.second
