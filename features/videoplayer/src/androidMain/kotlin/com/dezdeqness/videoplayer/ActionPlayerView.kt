package com.dezdeqness.videoplayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToLong

@Composable
fun ActionPlayerView(
    modifier: Modifier = Modifier,
    totalDuration: Long,
    currentTime: Long,
    onSeekTo: (Long) -> Unit = {},
    onOptionsClick: () -> Unit = {},
) {
    var localCurrentTime by remember { mutableLongStateOf(currentTime) }
    var isUserSliding by remember { mutableStateOf(false) }

    LaunchedEffect(currentTime) {
        if (!isUserSliding) {
            localCurrentTime = currentTime
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(localCurrentTime / 1000),
                color = Color.White,
            )
            Text(
                text = formatTime(totalDuration / 1000),
                color = Color.White,
            )
        }

        Slider(
            value = localCurrentTime.toFloat(),
            onValueChange = {
                isUserSliding = true
                localCurrentTime = it.roundToLong()
            },
            onValueChangeFinished = {
                isUserSliding = false
                onSeekTo(localCurrentTime)
            },
            valueRange = 0f..totalDuration.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { onOptionsClick() }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options")
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}
