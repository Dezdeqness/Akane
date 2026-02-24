package com.dezdeqness.videoplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.videoplayer.core.player.VideoPlayerController

@Composable
expect fun VideoPlayerView(modifier: Modifier, controller: VideoPlayerController)
