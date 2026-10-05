package com.dezdeqness.videoplayer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.videoplayer.VideoPlayerScreen
import com.dezdeqness.videoplayer.core.player.PlayerWindowEffect

@Composable
fun VideoPlayerPage(
    viewModel: VideoPlayerViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    PlayerWindowEffect()

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

