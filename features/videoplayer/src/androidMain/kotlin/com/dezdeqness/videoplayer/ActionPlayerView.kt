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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionPlayerView(
    modifier: Modifier = Modifier,
    totalDuration: Long,
    currentTime: Long,
    cachedTime: Long,
    onSeekTo: (Long) -> Unit = {},
    onOptionsClick: () -> Unit = {},
) {
    var localCurrentTime by remember { mutableLongStateOf(currentTime) }
    var isUserSliding by remember { mutableStateOf(false) }
    var localCachedTime by remember { mutableFloatStateOf(cachedTime.toFloat()) }

    LaunchedEffect(currentTime, cachedTime) {
        if (!isUserSliding) {
            localCurrentTime = currentTime
            localCachedTime = cachedTime.toFloat()
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
                localCachedTime = 0f
            },
            onValueChangeFinished = {
                isUserSliding = false
                onSeekTo(localCurrentTime)
            },
            valueRange = 0f..totalDuration.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            thumb = {
                val interactionSource = remember { MutableInteractionSource() }
                val modifier = Modifier
                    .padding(top = 3.dp)
                    .size(DpSize(10.dp, 10.dp))
                    .shadow(1.dp, CircleShape, clip = false)
                    .indication(
                        interactionSource = interactionSource,
                        indication = ripple(bounded = false, radius = 20.dp)
                    )
                SliderDefaults.Thumb(interactionSource = interactionSource, modifier = modifier)
            },
            track = { sliderPositions ->
                SliderDefaults.Track(
                    sliderState = sliderPositions,
                    modifier = Modifier
                        .height(4.dp)
                        .pulsatingEffect(
                            if (sliderPositions.value == 0f) 0f else sliderPositions.value / sliderPositions.valueRange.endInclusive,
                            if (localCachedTime == 0f) 0f else localCachedTime / sliderPositions.valueRange.endInclusive,
                        ),
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    drawStopIndicator = null,
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

fun Modifier.pulsatingEffect(
    currentPercentage: Float,
    cachedPercentage: Float,
    color: Color = Color.Red,
): Modifier = composed {
    var trackWidth by remember { mutableFloatStateOf(0f) }
    val thumbX by remember(currentPercentage) {
        mutableFloatStateOf(trackWidth * currentPercentage)
    }

    val endProgress by remember(cachedPercentage) {
        mutableFloatStateOf(trackWidth * cachedPercentage)
    }

    this then Modifier
        .onGloballyPositioned { coordinates ->
            trackWidth = coordinates.size.width.toFloat()
        }
        .drawWithContent {
            drawContent()

            val strokeWidth = size.height
            val y = size.height / 2f
            val startOffset = thumbX
            val endOffset = thumbX + endProgress

            drawLine(
                color = color,
                start = Offset(startOffset, y),
                end = Offset(endOffset, y),
                cap = StrokeCap.Round,
                strokeWidth = strokeWidth
            )
        }
}
