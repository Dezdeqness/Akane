package com.dezdeqness.stories.ui.playback

import com.dezdeqness.stories.ui.model.StoryUiModel
import com.dezdeqness.stories.ui.player.StoryPlayerPool
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

class StoryPlaybackController(
    private val pool: StoryPlayerPool,
    private val scope: CoroutineScope,
) {

    val players: StateFlow<Map<String, VideoPlayerManager>> = pool.players

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _playback = MutableStateFlow(StoryPlayback())

    val playback: StateFlow<StoryPlayback> = _playback.asStateFlow()

    private val activePlayer = MutableStateFlow<VideoPlayerManager?>(null)

    private val isResumed = MutableStateFlow(false)
    private val isHeld = MutableStateFlow(false)
    private val isTransitioning = MutableStateFlow(false)

    private val savedPositions = mutableMapOf<String, Long>()
    private var activeEpisodeId: String? = null
    private var activeStartPositionMs = 0L

    private var isPositionTrusted = false

    init {
        observePlayback()
        observePosition()
    }

    fun activate(story: StoryUiModel, keep: Set<String>) {
        val request = StoryPlayerPool.Request(
            episodeId = story.episodeId,
            url = story.videoUrl,
            startPositionMs = savedPositions[story.episodeId] ?: 0L,
        )
        val player = pool.acquire(active = request, keep = keep)

        activePlayer.value?.takeIf { it !== player }?.let { previous ->
            previous.pause()
            previous.setVolume(0f)
        }

        activeEpisodeId = story.episodeId
        activeStartPositionMs = request.startPositionMs
        isPositionTrusted = false
        _playback.value = StoryPlayback(episodeId = story.episodeId, positionMs = activeStartPositionMs)

        activePlayer.value = player
        applyVolume(player)
        applyPlayback(player, shouldPlay())
    }

    fun deactivate() {
        activeEpisodeId = null
        activePlayer.value?.pause()
        activePlayer.value = null
    }

    fun restart() {
        activePlayer.value?.seekTo(0)
    }

    fun onResumedChanged(isResumed: Boolean) {
        this.isResumed.value = isResumed
    }

    fun onHoldChanged(isHeld: Boolean) {
        this.isHeld.value = isHeld
    }

    fun onTransitionChanged(isTransitioning: Boolean) {
        this.isTransitioning.value = isTransitioning
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        activePlayer.value?.let(::applyVolume)
    }

    fun release() {
        pool.release()
    }

    private fun observePlayback() {
        scope.launch {
            combine(isResumed, isHeld, isTransitioning) { _, _, _ -> shouldPlay() }
                .distinctUntilChanged()
                .collect { play -> activePlayer.value?.let { applyPlayback(it, play) } }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observePosition() {
        scope.launch {
            activePlayer
                .flatMapLatest { player -> player?.playerState?.map { player to it } ?: emptyFlow() }
                .collect { (player, playerState) ->
                    val episodeId = activeEpisodeId ?: return@collect
                    if (!isPositionTrusted) {
                        if (abs(playerState.position - activeStartPositionMs) > TRUSTED_POSITION_WINDOW_MS) {
                            return@collect
                        }
                        isPositionTrusted = true
                    }

                    if (playerState.position >= LOOP_DURATION_MS) {
                        savedPositions[episodeId] = 0L
                        _playback.value = StoryPlayback(episodeId = episodeId, positionMs = 0L)
                        player.seekTo(0)
                        return@collect
                    }

                    savedPositions[episodeId] = playerState.position
                    _playback.value = StoryPlayback(
                        episodeId = episodeId,
                        positionMs = playerState.position,
                        isPlaying = playerState.isPlaying && !playerState.isBuffering,
                    )
                }
        }
    }

    private fun shouldPlay() = isResumed.value && !isHeld.value && !isTransitioning.value

    private fun applyPlayback(player: VideoPlayerManager, play: Boolean) {
        if (play) player.play() else player.pause()
    }

    private fun applyVolume(player: VideoPlayerManager) {
        player.setVolume(if (_isMuted.value) 0f else 1f)
    }

    companion object {
        const val LOOP_DURATION_MS = 30_000L
        private const val TRUSTED_POSITION_WINDOW_MS = 2_000L
    }
}

data class StoryPlayback(
    val episodeId: String? = null,
    val positionMs: Long = 0L,
    val isPlaying: Boolean = false,
)
