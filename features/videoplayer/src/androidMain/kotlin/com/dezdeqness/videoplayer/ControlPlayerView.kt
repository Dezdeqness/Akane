package com.dezdeqness.videoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun ControlPlayerView(
    modifier: Modifier = Modifier,
    onSeekBackward: () -> Unit = {},
    onSeekForward: () -> Unit = {},
    onPlayPauseToggle: (Boolean) -> Unit = {},
) {
    var isPlaying by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.Black.copy(0.5f), CircleShape)
                .clip(CircleShape)
                .clickable {
                    onSeekBackward()
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "-10",
                color = Color.White,
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(0.5f), CircleShape)
                .clip(CircleShape)
                .clickable {
                    isPlaying = !isPlaying
                    onPlayPauseToggle(isPlaying)
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Menu else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                tint = Color.White,
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.Black.copy(0.5f), CircleShape)
                .clip(CircleShape)
                .clickable {
                    onSeekForward()
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "+10",
                color = Color.White,
            )
        }
    }
}
