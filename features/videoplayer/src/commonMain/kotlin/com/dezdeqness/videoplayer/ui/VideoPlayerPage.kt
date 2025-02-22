package com.dezdeqness.videoplayer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.videoplayer.VideoPlayerScreen
import com.dezdeqness.videoplayer.core.rememberFullScreenState

@Composable
fun VideoPlayerPage(
    url: String,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
//    viewModel: VideoPlayerViewModel = koinViewModel(),
) {
//    val state by viewModel.videoPlayerStateFlow.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        VideoPlayerScreen(videoUrl = url, systemBarsControllerState = rememberFullScreenState())
    }
}

