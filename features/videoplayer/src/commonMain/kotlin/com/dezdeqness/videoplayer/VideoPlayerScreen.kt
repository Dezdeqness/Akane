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
import com.dezdeqness.videoplayer.ui.Status
import com.dezdeqness.videoplayer.ui.VideoPlayerView
import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
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
    onBackButtonClicked: () -> Unit = {},
) {
    val screenState by videoPlayerViewModel.screenState.collectAsStateWithLifecycle()
    val overlayState by videoPlayerViewModel.overlay.collectAsStateWithLifecycle()

    val videoData = screenState.videoData

    val featuresModifier = remember(videoPlayerViewModel) {
        videoPlayerViewModel.featuresModifier
    }

    val isControlsVisible = remember(screenState, overlayState) {
        overlayState.controlsVisible || screenState.isPlaylistVisible ||
                screenState.videoSpeedData.isVideoSpeedDropdownVisible ||
                screenState.qualityData.isQualityDropdownVisible
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(featuresModifier)
            .background(Color.Black)
    ) {
        if (videoData.status == Status.Initial || videoData.status == Status.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            return@Box
        }

        val currentEpisode = remember(videoData.episodes, screenState.currentEpisodeId) {
            videoData.episodes.first { it.id == screenState.currentEpisodeId }
        }

        VideoLayout(
            modifier = Modifier.fillMaxSize(),
            isControlsVisible = isControlsVisible,
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
                    controller = videoPlayerViewModel.controller
                )
            },
            actionPlayerView = {
                val playerState by videoPlayerViewModel.playerState.collectAsStateWithLifecycle()
                ActionPlayerView(
                    progressSlider = { modifier ->
                        ProgressSlider(
                            modifier = modifier,
                            totalDuration = playerState.duration,
                            currentTime = playerState.position,
                            cachedTime = playerState.buffered,
                            onSeekTo = { ms ->
                                videoPlayerViewModel.seekTo(ms)
                            },
                        )
                    },
                    qualityAction = {
                        Box {
                            TextButton(onClick = videoPlayerViewModel::onQualityActionClicked) {
                                Text(
                                    "${screenState.qualityData.currentVideoQuality.quality}",
                                    color = Color.White,
                                )
                            }
                            QualityDropdownMenu(
                                isExpanded = screenState.qualityData.isQualityDropdownVisible,
                                currentQuality = screenState.qualityData.currentVideoQuality,
                                onQualityChange = videoPlayerViewModel::onVideoQualitySelect,
                                onDismiss = videoPlayerViewModel::onQualityActionClosed,
                            )
                        }
                    },
                    speedAction = {
                        Box {
                            TextButton(onClick = videoPlayerViewModel::onVideoSpeedActionClicked) {
                                Text(
                                    "${screenState.videoSpeedData.videoSpeed.speed}x",
                                    color = Color.White,
                                )
                            }
                            VideoSpeedDropdownMenu(
                                isExpanded = screenState.videoSpeedData.isVideoSpeedDropdownVisible,
                                currentSpeed = screenState.videoSpeedData.videoSpeed,
                                onSpeedChange = videoPlayerViewModel::onVideoSpeedSelect,
                                onDismiss = videoPlayerViewModel::onVideoSpeedActionClosed,
                            )
                        }
                    },
                    playlistAction = {
                        AppIconButton(
                            icon = AkaneIcons.Menu,
                            onClick = videoPlayerViewModel::openPlaylist,
                            tint = Color.White,
                        )
                    }
                )
            },
            controlPlayerView = {
                val playerState by videoPlayerViewModel.playerState.collectAsStateWithLifecycle()

                ControlPlayerView(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.2f)),
                    isPlaying = playerState.isPlaying,
                    isLoading = playerState.isBuffering,
                    onSeekForward = {
                        videoPlayerViewModel.seekForward()
                    },
                    onSeekBackward = {
                        videoPlayerViewModel.seekBack()
                    },
                    onPlayPauseToggle = {
                        if (it) {
                            videoPlayerViewModel.pause()
                        } else {
                            videoPlayerViewModel.play()
                        }
                    }
                )
            }
        )
    }

    if (screenState.isPlaylistVisible) {
        PlaylistBottomSheet(
            episodes = screenState.videoData.episodes,
            currentEpisodeId = screenState.currentEpisodeId,
            onSelected = { id -> videoPlayerViewModel.selectEpisode(id) },
            onDismiss = { videoPlayerViewModel.closePlaylist() }
        )
    }
}
