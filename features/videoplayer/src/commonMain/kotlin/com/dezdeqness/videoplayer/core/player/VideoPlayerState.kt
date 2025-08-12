package com.dezdeqness.videoplayer.core.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class VideoPlayerState(
    private val player: VideoPlayer,
    private val scope: CoroutineScope,
    private val updatePlaybackStateInterval: Duration
) {
    var isPlaying by mutableStateOf(false)
        private set
    var durationPlayer by mutableLongStateOf(0)
        private set
    var currentPositionPlayer by mutableLongStateOf(0)
        private set
    val currentProgress by derivedStateOf {
        val progress = if (durationPlayer > 0) (currentPositionPlayer.toFloat() / durationPlayer) else 0f
        if (progress.isNaN() || progress.isInfinite()) 0f else progress
    }
    var isLoading by mutableStateOf(false)
        private set
    var isBuffering by mutableStateOf(false)
        private set
    var bufferedDuration by mutableLongStateOf(0L)
        private set
    private var playbackStateJob: Job? = null

    init {
        player.addListener(object : VideoPlayerListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                this@VideoPlayerState.isPlaying = isPlaying
                this@VideoPlayerState.durationPlayer = player.getDuration()
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                this@VideoPlayerState.isLoading = isLoading
            }

            override fun isBuffering(buffering: Boolean) {
                isBuffering = buffering
                playbackStateJob?.cancel()
                playbackStateJob = scope.launch {
                    while (true) {
                        currentPositionPlayer = player.getCurrentPosition()
                        bufferedDuration = player.getTotalBufferedDuration()
                        delay(updatePlaybackStateInterval)
                    }
                }
            }
        })
    }

    fun play() = player.play()
    fun pause() = player.pause()
    fun stop() = player.stop()
    fun release() = player.release()
    fun seekTo(progress: Float) = player.seekTo((progress * durationPlayer).roundToLong())
    fun seekByTimestamp(timestamp: Long) = player.seekTo(timestamp)
    fun setSpeed(speed: Float) = player.setPlaybackSpeed(speed)
    fun seekForward() {
        player.seekForward()
    }
    fun seekBack() {
        player.seekBack()
    }
    fun setVideoItems(mediaItems: List<String>, startIndex: Int, startPositionMs: Long) {
        player.setVideoItems(mediaItems, startIndex, startPositionMs)
    }

    fun getPlayer(): VideoPlayer = player
}

@Composable
expect fun rememberVideoPlayerState(
    lifecycleAware: Boolean = true,
    updatePlaybackStateInterval: Duration = 500.milliseconds
): VideoPlayerState