package com.dezdeqness.videoplayer

import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.dezdeqness.videoplayer.core.FullScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayerScreen(
    videoUrl: String,
    systemBarsControllerState: FullScreenState,
) {
    val context = LocalContext.current

    val playerView = remember { TextureView(context) }

    val exoPlayer = remember {
        ExoPlayer
            .Builder(context)
            .setSeekForwardIncrementMs(10000L)
            .setSeekBackIncrementMs(10000L)
            .build().apply {
                val mediaItem = MediaItem.fromUri(videoUrl.toUri())
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
    }

    val playerState = rememberVideoPlayerState(
        exoPlayer = exoPlayer,
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.clearVideoTextureView(playerView)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(16 / 9F),
            factory = {
                playerView.apply {
                    exoPlayer.setVideoTextureView(playerView)
                }
            }
        )
        AnimatedVisibility(
            visible = systemBarsControllerState.isSystemBarVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 300, easing = LinearEasing)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ControlPlayerView(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.2f))
                        .align(Alignment.Center),
                    isPlaying = playerState.isPlaying,
                    isLoading = false,
                    onSeekForward = {
                        playerState.seekForward()
                    },
                    onSeekBackward = {
                        playerState.seekBack()
                    },
                    onPlayPauseToggle = {
                        if (it) {
                            playerState.pause()
                        } else {
                            playerState.play()
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = systemBarsControllerState.isSystemBarVisible,
            enter = slideInVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) { it },
            exit = slideOutVertically(animationSpec = tween(durationMillis = 300)) { it }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ActionPlayerView(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    totalDuration = playerState.durationPlayer,
                    currentTime = playerState.currentPositionPlayer,
                    onSeekTo = {
                        playerState.seekByTimestamp(it)
                    }
                )
            }
        }

    }
}

class VideoPlayerState(
    private val exoPlayer: ExoPlayer,
    private val scope: CoroutineScope,
    private val updatePlaybackStateInterval: Duration
) : ExoPlayer by exoPlayer, Player.Listener {
    @get:JvmName("isPlayingX")
    var isPlaying by mutableStateOf(false)
        private set
    var durationPlayer by mutableLongStateOf(0)
        private set
    var currentPositionPlayer by mutableLongStateOf(0)
        private set
    val currentProgress by derivedStateOf {
        val progress = (currentPositionPlayer / durationPlayer).toFloat()
        if (progress.isNaN() || progress.isInfinite()) 0f
        else progress
    }

    @get:JvmName("getVolumeX")
    @set:JvmName("setVolumeX")
    var volume by mutableFloatStateOf(getVolume())
        private set
    val isMuted by derivedStateOf { volume == 0f }

    @get:JvmName("isLoadingX")
    var isLoading by mutableStateOf(false)
        private set
    var isBuffering by mutableStateOf(false)
        private set
    var bufferedPercentage by mutableFloatStateOf(0f)
        private set
    private var playbackStateJob: Job? = null

    init {
        addListener(this)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        this.isPlaying = isPlaying
        this.durationPlayer = getDuration()
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)

        this.isLoading = isLoading
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        isBuffering = playbackState == Player.STATE_BUFFERING

        playbackStateJob?.cancel()
        playbackStateJob = scope.launch {
            while (true) {
                this@VideoPlayerState.currentPositionPlayer = getCurrentPosition()
                this@VideoPlayerState.bufferedPercentage = getBufferedPercentage().toFloat()
                delay(updatePlaybackStateInterval)
            }
        }
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume
        this.volume = volume
    }

    fun seekTo(progress: Float) {
        seekTo((progress * durationPlayer).roundToLong())
    }

    fun seekByTimestamp(timestamp: Long) {
        seekTo(timestamp)
    }
}

@Composable
fun rememberVideoPlayerState(
    lifecycleAware: Boolean = true,
    exoPlayer: ExoPlayer,
    updatePlaybackStateInterval: Duration = 500.milliseconds
): VideoPlayerState {
    val scope = rememberCoroutineScope()
    val videoPlayerState = remember {
        VideoPlayerState(exoPlayer, scope, updatePlaybackStateInterval)
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    if (lifecycleAware) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> videoPlayerState.play()
                    Lifecycle.Event.ON_PAUSE -> videoPlayerState.pause()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                videoPlayerState.release()
            }
        }
    }

    return videoPlayerState
}
