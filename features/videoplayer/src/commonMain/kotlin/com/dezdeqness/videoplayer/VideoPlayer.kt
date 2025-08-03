package com.dezdeqness.videoplayer

import androidx.compose.runtime.Composable
import com.dezdeqness.videoplayer.core.FullScreenState
import com.dezdeqness.videoplayer.ui.VideoPlayerViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun VideoPlayerScreen(
    id: Long,
    episodeId: String,
    videoPlayerViewModel: VideoPlayerViewModel = koinViewModel(),
    systemBarsControllerState: FullScreenState,
    onBackButtonClicked: () -> Unit = {},
)
