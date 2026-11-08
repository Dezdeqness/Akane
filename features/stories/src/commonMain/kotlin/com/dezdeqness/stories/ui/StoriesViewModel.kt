package com.dezdeqness.stories.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.stories.contract.repository.StoriesRepository
import com.dezdeqness.stories.ui.navigation.StoriesNavigator
import com.dezdeqness.stories.ui.playback.StoryPlayback
import com.dezdeqness.stories.ui.playback.StoryPlaybackController
import com.dezdeqness.stories.ui.player.StoryPlayerPool
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class StoriesViewModel(
    pool: StoryPlayerPool,
    initialCollectionIndex: Int,
    private val storiesRepository: StoriesRepository,
    private val uiMapper: StoriesUiMapper,
    private val dispatchers: CoroutineDispatcherProvider,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val navigator = StoriesNavigator(initialCollection = initialCollectionIndex)
    private val playback = StoryPlaybackController(pool = pool, scope = viewModelScope)

    private val reloadTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private var followedEpisodeId: String? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val content: Flow<StoriesState> = reloadTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            storiesRepository.getStories()
                .map { result -> result.map { cached -> cached.value.map(uiMapper::map) } }
                .flowOn(dispatchers.io())
                .scan(StoriesState(status = StoriesState.Status.Loading)) { previous, result ->
                    result.fold(
                        onSuccess = { collections ->
                            StoriesState(
                                status = if (collections.isEmpty()) {
                                    StoriesState.Status.Empty
                                } else {
                                    StoriesState.Status.Loaded
                                },
                                collections = collections,
                            )
                        },
                        onFailure = { throwable ->
                            captureError(throwable)
                            if (previous.collections.isEmpty()) {
                                previous.copy(status = StoriesState.Status.Error)
                            } else {
                                previous
                            }
                        },
                    )
                }
        }

    val state: StateFlow<StoriesState> = combine(content, navigator.position, navigator::resolve)
        .onEach(::followActiveStory)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = StoriesState(currentCollection = initialCollectionIndex),
        )

    val events: Flow<StoriesEvent> = navigator.events

    val players: StateFlow<Map<String, VideoPlayerManager>> = playback.players
    val playbackState: StateFlow<StoryPlayback> = playback.playback
    val isMuted: StateFlow<Boolean> = playback.isMuted

    fun retry() {
        reloadTrigger.tryEmit(Unit)
    }

    fun onNext() = navigator.next(state.value)

    fun onPrevious() {
        if (!navigator.previous(state.value)) playback.restart()
    }

    fun onCollectionSettled(index: Int) = navigator.onCollectionSettled(state.value, index)

    fun onTransitionChanged(isTransitioning: Boolean) = playback.onTransitionChanged(isTransitioning)

    fun onHoldChanged(isHeld: Boolean) = playback.onHoldChanged(isHeld)

    fun onResumedChanged(isResumed: Boolean) = playback.onResumedChanged(isResumed)

    fun onMuteClicked() = playback.toggleMute()

    override fun onCleared() {
        playback.release()
    }

    private fun followActiveStory(state: StoriesState) {
        val story = state.activeStory()
        if (story?.episodeId == followedEpisodeId) return
        followedEpisodeId = story?.episodeId

        if (story == null) {
            playback.deactivate()
        } else {
            val neighbours = listOfNotNull(state.nextStory(), state.previousStory())
            playback.activate(story, keep = neighbours.map { it.episodeId }.toSet())
        }
    }

    private fun captureError(throwable: Throwable) {
        errorReporter.captureException(
            throwable = throwable,
            message = "Stories load failed",
            tags = mapOf("feature" to "stories"),
        )
    }
}
