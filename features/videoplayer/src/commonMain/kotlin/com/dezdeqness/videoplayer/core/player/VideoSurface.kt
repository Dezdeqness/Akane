package com.dezdeqness.videoplayer.core.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
expect fun VideoSurface(engine: VideoPlayerManager, modifier: Modifier = Modifier)
