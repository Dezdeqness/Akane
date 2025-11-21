package com.dezdeqness.videoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.videoplayer.core.FullScreenState
import com.dezdeqness.videoplayer.core.player.VideoPlayerView
import com.dezdeqness.videoplayer.core.player.rememberVideoPlayerState
import com.dezdeqness.videoplayer.ui.Status
import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
import com.dezdeqness.videoplayer.ui.VideoQuality
import com.dezdeqness.videoplayer.ui.composables.ActionPlayerView
import com.dezdeqness.videoplayer.ui.composables.ControlPlayerView
import com.dezdeqness.videoplayer.ui.composables.ProgressSlider
import com.dezdeqness.videoplayer.ui.composables.VideoLayout
import com.dezdeqness.videoplayer.ui.composables.bottomsheet.PlaylistBottomSheet
import com.dezdeqness.videoplayer.ui.composables.dropdown.QualityDropdownMenu
import com.dezdeqness.videoplayer.ui.composables.dropdown.VideoSpeedDropdownMenu
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoPlayerViewModel: VideoPlayerViewModel = koinViewModel(),
    systemBarsControllerState: FullScreenState,
    onBackButtonClicked: () -> Unit = {},
) {
    val state by videoPlayerViewModel.videoPlayerStateFlow.collectAsStateWithLifecycle()

    val videoData = state.videoData

    val videoSpeedData = state.videoSpeedData

    val playerState = rememberVideoPlayerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (videoData.status == Status.Initial || videoData.status == Status.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            return@Box
        }

        val currentEpisode = remember(videoData.episodes, state.currentEpisodeId) {
            videoData.episodes.first { it.id == state.currentEpisodeId }
        }

        LaunchedEffect(videoData.episodes, state.qualityData.currentVideoQuality) {
            if (videoData.episodes.isEmpty()) return@LaunchedEffect
            val mediaItems = videoData.episodes.map {
                when (state.qualityData.currentVideoQuality) {
                    VideoQuality.q480 -> it.hls480.orEmpty()
                    VideoQuality.q720 -> it.hls720.orEmpty()
                    VideoQuality.q1080 -> it.hls1080.orEmpty()
                }
            }
            val startIndex = videoData.episodes.indexOfFirst { it.id == state.currentEpisodeId }
            playerState.setVideoItems(mediaItems, startIndex, playerState.currentPositionPlayer)
            playerState.play()
        }

        LaunchedEffect(videoData.episodes, state.currentEpisodeId) {
            if (videoData.episodes.isEmpty()) return@LaunchedEffect
            val startIndex = videoData.episodes.indexOfFirst { it.id == state.currentEpisodeId }
            playerState.seekByTimestamp(0)
            if (startIndex >= 0) {
                val mediaItems = videoData.episodes.map {
                    when (state.qualityData.currentVideoQuality) {
                        VideoQuality.q480 -> it.hls480.orEmpty()
                        VideoQuality.q720 -> it.hls720.orEmpty()
                        VideoQuality.q1080 -> it.hls1080.orEmpty()
                    }
                }
                playerState.setVideoItems(mediaItems, startIndex, 0)
            }
            playerState.play()
        }

        LaunchedEffect(videoSpeedData) {
            playerState.setSpeed(videoSpeedData.videoSpeed.speed)
        }

        VideoLayout(
            modifier = Modifier.fillMaxSize(),
            isSystemBarVisible = systemBarsControllerState.isSystemBarVisible,
            appbar = {
                AppToolbar(
                    modifier = Modifier.fillMaxWidth(),
//                        .padding(
//                            top = if (systemBarsControllerState.isSystemBarVisible) WindowInsets
//                                .systemBars
//                                .only(WindowInsetsSides.Top)
//                                .asPaddingValues()
//                                .calculateTopPadding() else {
//                                0.dp
//                            }
//                        )
                    title = {
                        Column {
                            Text(
                                videoData.title,
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
                    navigation = {
                        AppIconButton(
                            icon = AkaneIcons.Back,
                            tint = Color.White,
                            onClick = onBackButtonClicked
                        )
                    }
                )
            },
            videoPlayerView = { modifier ->
                VideoPlayerView(
                    modifier = modifier.aspectRatio(16 / 9F),
                    playerState = playerState,
                )
            },
            actionPlayerView = {
                ActionPlayerView(
                    progressSlider = { modifier ->
                        ProgressSlider(
                            modifier = modifier,
                            totalDuration = playerState.durationPlayer,
                            currentTime = playerState.currentPositionPlayer,
                            cachedTime = playerState.bufferedDuration,
                            onSeekTo = {
                                playerState.seekByTimestamp(it)
                            }
                        )
                    },
                    qualityAction = {
                        Box {
                            TextButton(
                                onClick = videoPlayerViewModel::onQualityActionClicked
                            ) {
                                Text(
                                    "${state.qualityData.currentVideoQuality.quality}",
                                    color = Color.White,
                                )
                            }
                            QualityDropdownMenu(
                                isExpanded = state.qualityData.isQualityDropdownVisible,
                                currentQuality = state.qualityData.currentVideoQuality,
                                onQualityChange = videoPlayerViewModel::onVideoQualitySelect,
                                onDismiss = videoPlayerViewModel::onQualityActionClosed,
                            )
                        }
                    },
                    speedAction = {
                        Box {
                            TextButton(
                                onClick = videoPlayerViewModel::onVideoSpeedActionClicked,
                            ) {
                                Text(
                                    "${videoSpeedData.videoSpeed.speed}x",
                                    color = Color.White,
                                )
                            }
                            VideoSpeedDropdownMenu(
                                isExpanded = videoSpeedData.isVideoSpeedDropdownVisible,
                                currentSpeed = videoSpeedData.videoSpeed,
                                onSpeedChange = videoPlayerViewModel::onVideoSpeedSelect,
                                onDismiss = videoPlayerViewModel::onVideoSpeedActionClosed,
                            )
                        }
                    },
                    playlistAction = {
                        AppIconButton(
                            icon = AkaneIcons.Menu,
                            onClick = videoPlayerViewModel::onPlaylistActionClicked,
                            tint = Color.White,
                        )
                    }
                )
            },
            controlPlayerView = {
                ControlPlayerView(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.2f)),
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
        )

        if (state.isPlaylistBottomSheetVisible) {
            PlaylistBottomSheet(
                modifier = Modifier,
                episodes = state.videoData.episodes,
                currentEpisodeId = state.currentEpisodeId,
                onSelected = { id ->
                    videoPlayerViewModel.onSelectEpisode(id)
                },
                onDismiss = {
                    videoPlayerViewModel.onPlaylistActionClosed()
                }
            )
        }

    }
}
