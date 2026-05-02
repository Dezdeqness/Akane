package com.dezdeqness.videoplayer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.videoplayer.VideoPlayerScreen

@Composable
fun VideoPlayerPage(
    viewModel: VideoPlayerViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        VideoPlayerScreen(
            videoPlayerViewModel = viewModel,
            onBackButtonClicked = onBackPressed,
        )
    }
}

