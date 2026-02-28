package com.dezdeqness.videoplayer.core.player.composables

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.dezdeqness.videoplayer.core.player.data.SkipRange
import kotlin.math.abs
import kotlin.math.roundToLong

data class ProgressSliderState(
    val position: Long,
    val duration: Long,
    val buffered: Long,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoProgressSlider(
    state: ProgressSliderState,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    opening: SkipRange? = null,
    ending: SkipRange? = null,
) {
    var localPosition by remember { mutableLongStateOf(state.position) }
    var localBuffered by remember { mutableFloatStateOf(state.buffered.toFloat()) }
    var isUserSliding by remember { mutableStateOf(false) }
    var seekTarget by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(state.position, state.buffered) {
        if (!isUserSliding) {
            val target = seekTarget
            if (target != null && abs(state.position - target) > 1500) {
                localBuffered = state.buffered.toFloat()
            } else {
                seekTarget = null
                localPosition = state.position
                localBuffered = state.buffered.toFloat()
            }
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = formatDuration(localPosition / 1000), color = Color.White)
            Text(text = formatDuration(state.duration / 1000), color = Color.White)
        }

        Slider(
            value = localPosition.toFloat(),
            onValueChange = {
                isUserSliding = true
                localPosition = it.roundToLong()
                localBuffered = 0f
                println("position: " + it.roundToLong())
            },
            onValueChangeFinished = {
                isUserSliding = false
                seekTarget = localPosition
                println("localPosition: " + localPosition)

                onSeek(localPosition)
            },
            valueRange = 0f..abs(state.duration).toFloat(),
            thumb = {
                val interactionSource = remember { MutableInteractionSource() }
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .size(DpSize(10.dp, 10.dp))
                        .shadow(1.dp, CircleShape, clip = false)
                        .indication(interactionSource, ripple(bounded = false, radius = 20.dp)),
                )
            },
            track = { sliderState ->
                val end = sliderState.valueRange.endInclusive.coerceAtLeast(1f)
                val currentMs = sliderState.value.toLong()
                val totalMs = end.toLong()

                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier
                        .height(4.dp)
                        .bufferedTrack(
                            currentPct = (sliderState.value / end).coerceIn(0f, 1f),
                            bufferedPct = (localBuffered / end).coerceIn(0f, 1f),
                        )
                        .skipMarkersOnTrack(
                            totalMs = totalMs,
                            currentMs = currentMs,
                            opening = opening,
                            ending = ending,
                        ),
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    drawStopIndicator = null,
                )
            }
        )
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}

@Composable
private fun Modifier.bufferedTrack(
    currentPct: Float,
    bufferedPct: Float,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
): Modifier = composed {
    drawWithContent {
        drawContent()
        val startX = size.width * currentPct.coerceIn(0f, 1f)
        val endX = size.width * bufferedPct.coerceIn(0f, 1f)
        if (endX <= startX) return@drawWithContent
        drawLine(
            color = color,
            start = Offset(startX, size.height / 2f),
            end = Offset(endX, size.height / 2f),
            cap = StrokeCap.Round,
            strokeWidth = size.height,
        )
    }
}

fun Modifier.skipMarkersOnTrack(
    totalMs: Long,
    currentMs: Long,
    opening: SkipRange?,
    ending: SkipRange?,
    markerWidth: Dp = 2.dp,
    markerHeight: Dp = 4.dp,
    gapFromTrackTop: Dp = 6.dp,
    color: Color = Color.White,
): Modifier = drawWithContent {
    drawContent()
    if (totalMs <= 0L) return@drawWithContent

    val w = size.width
    val h = size.height

    val currentPct = (currentMs.toFloat() / totalMs).coerceIn(0f, 1f)
    val currentX = w * currentPct

    fun drawMarkerAtMs(ms: Long) {
        val pct = (ms.toFloat() / totalMs).coerceIn(0f, 1f)
        val x = w * pct
        if (x <= currentX) return

        val top = h - markerHeight.toPx() / 2f - gapFromTrackTop.toPx() / 2f
        val bottom = top + markerHeight.toPx()

        drawLine(
            color = color,
            start = Offset(x, top),
            end = Offset(x, bottom),
            strokeWidth = markerWidth.toPx()
        )
    }

    opening?.startMs?.let(::drawMarkerAtMs)
    ending?.startMs?.let(::drawMarkerAtMs)

    opening?.endMs?.let(::drawMarkerAtMs)
    ending?.endMs?.let(::drawMarkerAtMs)
}
