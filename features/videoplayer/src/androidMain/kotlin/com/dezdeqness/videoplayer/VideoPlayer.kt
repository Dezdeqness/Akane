@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.dezdeqness.videoplayer

import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.dezdeqness.videoplayer.core.FullScreenState
import com.dezdeqness.videoplayer.ui.PlaylistBottomSheet
import com.dezdeqness.videoplayer.ui.Status
import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val standardEnter = fadeIn(
    tween(
        durationMillis = 250,
        delayMillis = 0,
        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f),
    )
)

private val standardExit = fadeOut(
    tween(
        durationMillis = 200,
        delayMillis = 0,
        easing = CubicBezierEasing(0.3f, 0.0f, 1f, 1f),
    )
)

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
actual fun VideoPlayerScreen(
    id: Long,
    episodeId: String,
    videoPlayerViewModel: VideoPlayerViewModel,
    systemBarsControllerState: FullScreenState,
    onBackButtonClicked: () -> Unit,
) {
    val state by videoPlayerViewModel.videoPlayerStateFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val playerView = remember { TextureView(context) }

    val exoPlayer = remember {
        ExoPlayer
            .Builder(context)
            .setSeekForwardIncrementMs(10000L)
            .setSeekBackIncrementMs(10000L)
            .build()
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

        if (state.status == Status.Initial || state.status == Status.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            return@Box
        }

        val currentEpisode = remember(state.episodes, state.currentEpisodeId) {
            state.episodes.first { it.id == state.currentEpisodeId }
        }

        LaunchedEffect(state.episodes, state.currentEpisodeId) {

            if (state.episodes.isEmpty()) return@LaunchedEffect
            exoPlayer.apply {
                val mediaItems = state.episodes.map { MediaItem.fromUri(it.hls720!!.toUri()) }
                val startIndex = state.episodes.indexOfFirst { it.id == state.currentEpisodeId }
                setMediaItems(mediaItems, startIndex, 0)
                prepare()

                playWhenReady = true
            }
        }

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
            enter = standardEnter,
            exit = standardExit,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
//                        .padding(
//                            top = if (systemBarsControllerState.isSystemBarVisible) WindowInsets
//                                .systemBars
//                                .only(WindowInsetsSides.Top)
//                                .asPaddingValues()
//                                .calculateTopPadding() else {
//                                0.dp
//                            }
//                        )
                    ,
                    title = {
                        Column {
                            Text(
                                state.title,
                                style = MaterialTheme.typography.headlineMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                currentEpisode.name.ifEmpty { "${currentEpisode.ordinal} эпизод" },
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        titleContentColor = Color.White,
                        containerColor = Color.Transparent,
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackButtonClicked) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                tint = Color.White,
                                contentDescription = null,
                            )
                        }
                    }
                )
            }
        }
        AnimatedVisibility(
            visible = systemBarsControllerState.isSystemBarVisible,
            enter = standardEnter,
            exit = standardExit,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ControlPlayerView(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.2f))
                        .align(Alignment.Center),
                    isPlaying = playerState.isPlaying,
                    isLoading = playerState.isBuffering,
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
            enter = standardEnter,
            exit = standardExit,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ActionPlayerView(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    totalDuration = playerState.durationPlayer,
                    currentTime = playerState.currentPositionPlayer,
                    cachedTime = playerState.bufferedDuration,
                    onSeekTo = {
                        playerState.seekByTimestamp(it)
                    },
                    onPlaylistClick = {
                        videoPlayerViewModel.onPlaylistActionClicked()
                    }
                )
            }
        }

        if (state.isPlaylistBottomSheetVisible) {
            PlaylistBottomSheet(
                modifier = Modifier,
                episodes = state.episodes,
                currentEpisodeId = state.currentEpisodeId,
                onSelected = { id ->
                    videoPlayerViewModel.selectEpisode(id)
                },
                onDismiss = {
                    videoPlayerViewModel.onPlaylistActionClosed()
                }
            )
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
    var bufferedDuration by mutableLongStateOf(0L)
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
                this@VideoPlayerState.bufferedDuration = getTotalBufferedDuration()
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
