package com.dezdeqness.videoplayer.core.player

import com.dezdeqness.videoplayer.core.OverlayState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VideoPlayerController(
    val player: VideoPlayer,
    scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(VideoPlayerUiState())
    val state: StateFlow<VideoPlayerUiState> = _state

    private val _overlay = MutableStateFlow(OverlayState())
    val overlay: StateFlow<OverlayState> = _overlay

    init {
        scope.launch { player.events.collect { reduce(it) } }
    }

    private fun reduce(event: PlayerEvent) {
        _state.update { old ->
            when (event) {
                is PlayerEvent.IsPlaying -> old.copy(isPlaying = event.value)
                is PlayerEvent.IsBuffering -> old.copy(isBuffering = event.value)
                is PlayerEvent.DurationChanged -> old.copy(
                    duration = event.durationMs.coerceAtLeast(
                        0
                    )
                )

                is PlayerEvent.PositionChanged -> old.copy(
                    position = event.positionMs.coerceAtLeast(
                        0
                    )
                )

                is PlayerEvent.BufferedChanged -> old.copy(
                    buffered = event.bufferedMs.coerceAtLeast(
                        0
                    )
                )

                is PlayerEvent.PlaybackEnded -> old.copy(isPlaying = false)
                is PlayerEvent.Error -> old.copy(error = event.message)
            }
        }
    }

    fun play() = player.play()
    fun pause() = player.pause()
    fun stop() = player.stop()
    fun release() = player.release()

    fun seekTo(ms: Long) = player.seekTo(ms)
    fun seekBack() = player.seekBack()
    fun seekForward() = player.seekForward()
    fun setSpeed(speed: Float) = player.setPlaybackSpeed(speed.coerceIn(0.25f, 3f))
    fun setVolume(volume: Float) = player.setVolume(volume.coerceIn(0f, 1f))

    fun showControls() = _overlay.update { it.copy(controlsVisible = true) }
    fun hideControls() = _overlay.update { it.copy(controlsVisible = false) }
}
