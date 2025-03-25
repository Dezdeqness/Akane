package com.dezdeqness.videoplayer

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
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
            modifier = Modifier.fillMaxWidth(),
            thumb = {
                val thumbSize = DpSize(20.dp, 20.dp)
                val interactionSource = remember { MutableInteractionSource() }
                val modifier =
                    Modifier.size(thumbSize)
                        .shadow(1.dp, CircleShape, clip = false)
                        .indication(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = false, radius = 20.dp)
                        )
                SliderDefaults.Thumb(interactionSource = interactionSource, modifier = modifier)
            },
            track = { sliderPositions ->
                val trackHeight = 4.dp
                val modifier = Modifier.height(trackHeight)
                SliderDefaults.Track(
                    sliderState = sliderPositions,
                    modifier = modifier,
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    drawStopIndicator = null
                )
            },
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
