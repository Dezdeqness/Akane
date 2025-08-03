package com.dezdeqness.videoplayer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.videoplayer.navigation.EPISODE_ID
import com.dezdeqness.videoplayer.navigation.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class VideoPlayerViewModel(
    private val releaseRepository: ReleaseRepository,
    private val videoPlayerUiMapper: VideoPlayerUiMapper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var releaseId = savedStateHandle.get<Long>(ID) ?: -1
    private var episodeId = savedStateHandle.get<String>(EPISODE_ID)

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val videoPlayerStateFlow: StateFlow<VideoPlayerState> = loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val result = releaseRepository.getReleaseById(releaseId)

                emit(
                    LoadResult(
                        event = event,
                        result = result,
                    )
                )
            }.flowOn(Dispatchers.IO)
        }
        .scan(VideoPlayerState()) { previous, loadResult ->
            val result = loadResult.result

            result.onSuccess { response ->
                val episodes = response.episodes.map(videoPlayerUiMapper::map).sortedBy { it.ordinal }

                return@scan previous.copy(
                    title = response.name,
                    currentEpisodeId = episodeId ?: episodes.first().id,
                    episodes = episodes,
                    status = Status.Loaded,
                )
            }

            result.onFailure { error ->
                Logger.e(
                    tag = TAG,
                    messageString = "Load error: ${error.message.orEmpty()}",
                )

                return@scan previous.copy(
                    status = Status.Error,
                )
            }

            previous
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = VideoPlayerState()
        )


    private sealed class LoadEvent() {
        data object Initial : LoadEvent()
    }

    private data class LoadResult(
        val event: LoadEvent,
        val result: Result<ReleaseDetailsEntity>,
    )

    companion object {
        private const val TAG = "VideoPlayerViewModel"
    }
}
