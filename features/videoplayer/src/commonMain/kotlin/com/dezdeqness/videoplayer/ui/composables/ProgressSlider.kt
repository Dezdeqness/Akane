package com.dezdeqness.videoplayer.ui.composables

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressSlider(
    modifier: Modifier = Modifier,
    totalDuration: Long,
    currentTime: Long,
    cachedTime: Long,
    onSeekTo: (Long) -> Unit,
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

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
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
            valueRange = 0f..abs(totalDuration).toFloat(),
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
                val end = sliderPositions.valueRange.endInclusive.coerceAtLeast(1f)

                val currentPct = (sliderPositions.value / end).coerceIn(0f, 1f)
                val cachedPct = (localCachedTime / end).coerceIn(0f, 1f)

                SliderDefaults.Track(
                    sliderState = sliderPositions,
                    modifier = Modifier
                        .height(4.dp)
                        .bufferedEffect(
                            currentPercentage = currentPct,
                            cachedPercentage = cachedPct,
                        ),
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    drawStopIndicator = null,
                )
            }
        )
    }
}

fun formatTime(duration: Long): String {
    val minutes = duration / 60
    val seconds = duration % 60
    val min = minutes.toString().padStart(2, '0')
    val sec = seconds.toString().padStart(2, '0')
    return "$min:$sec"
}

@Composable
fun Modifier.bufferedEffect(
    currentPercentage: Float,
    cachedPercentage: Float,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
): Modifier = composed {
    this.drawWithContent {
        drawContent()

        val cur = currentPercentage.coerceIn(0f, 1f)
        val cache = cachedPercentage.coerceIn(0f, 1f)

        val startX = size.width * cur
        val endX = size.width * cache

        if (endX <= startX) return@drawWithContent

        val strokeWidth = size.height
        val y = size.height / 2f

        drawLine(
            color = color,
            start = Offset(startX, y),
            end = Offset(endX, y),
            cap = StrokeCap.Round,
            strokeWidth = strokeWidth,
        )
    }
}