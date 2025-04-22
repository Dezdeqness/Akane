package com.dezdeqness.videoplayer

import androidx.compose.runtime.Composable
import com.dezdeqness.videoplayer.core.FullScreenState

@Composable
expect fun VideoPlayerScreen(
    videoUrl: String,
    systemBarsControllerState: FullScreenState,
    onBackButtonClicked: () -> Unit = {},
)
