package com.dezdeqness.videoplayer.core.player.composables.slider

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dezdeqness.videoplayer.core.player.data.SkipRange
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private val SliderTextColor = Color.White
private val SliderThumbColor = Color.White

private val DefaultTrackHeight = 4.dp
private val ActiveTrackHeight = 6.dp
private val DefaultThumbSize = 10.dp
private val ActiveThumbSize = 14.dp

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
    opening: SkipRange? = null,
    ending: SkipRange? = null,
    modifier: Modifier = Modifier,
) {
    val safeDuration = state.duration.coerceAtLeast(1L)

    var localPosition by remember(safeDuration) {
        mutableLongStateOf(state.position.coerceIn(0L, safeDuration))
    }
    var localBuffered by remember(safeDuration) {
        mutableLongStateOf(state.buffered.coerceIn(0L, safeDuration))
    }
    var isUserSliding by remember { mutableStateOf(false) }
    var pendingSeekTarget by remember { mutableStateOf<Long?>(null) }
    var trackWidthPx by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.position, state.buffered, state.duration, isUserSliding, pendingSeekTarget) {
        if (isUserSliding) return@LaunchedEffect

        val incomingPosition = state.position.coerceIn(0L, safeDuration)
        val incomingBuffered = state.buffered.coerceIn(0L, safeDuration)

        if (pendingSeekTarget != null && abs(incomingPosition - pendingSeekTarget!!) > 1500L) {
            localBuffered = incomingBuffered
        } else {
            pendingSeekTarget = null
            localPosition = incomingPosition
            localBuffered = incomingBuffered
        }
    }

    Column(modifier = modifier) {
        SliderTimeLabels(
            currentPosition = state.position,
            duration = safeDuration,
        )

        SliderContent(
            localPosition = localPosition,
            localBuffered = localBuffered,
            safeDuration = safeDuration,
            opening = opening,
            ending = ending,
            isUserSliding = isUserSliding,
            trackWidthPx = trackWidthPx,
            onTrackWidthMeasured = { trackWidthPx = it },
            onValueChange = { newValue ->
                isUserSliding = true
                localPosition = newValue.roundToLong().coerceIn(0L, safeDuration)
            },
            onValueChangeFinished = {
                isUserSliding = false
                pendingSeekTarget = localPosition
                onSeek(localPosition)
            },
        )
    }
}

@Composable
private fun SliderTimeLabels(
    currentPosition: Long,
    duration: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = formatDuration(currentPosition / 1000),
            color = SliderTextColor,
        )
        Text(
            text = formatDuration(duration / 1000),
            color = SliderTextColor,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SliderContent(
    localPosition: Long,
    localBuffered: Long,
    safeDuration: Long,
    opening: SkipRange?,
    ending: SkipRange?,
    isUserSliding: Boolean,
    trackWidthPx: Int,
    onTrackWidthMeasured: (Int) -> Unit,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackHeight by animateDpAsState(
        targetValue = if (isUserSliding) ActiveTrackHeight else DefaultTrackHeight,
        label = "video_slider_track_height",
    )

    val thumbSize by animateDpAsState(
        targetValue = if (isUserSliding) ActiveThumbSize else DefaultThumbSize,
        label = "video_slider_thumb_size",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        SliderTrackWithThumb(
            localPosition = localPosition,
            localBuffered = localBuffered,
            safeDuration = safeDuration,
            opening = opening,
            ending = ending,
            trackWidthPx = trackWidthPx,
            trackHeight = trackHeight,
            thumbSize = thumbSize,
            onTrackWidthMeasured = onTrackWidthMeasured,
        )

        if (isUserSliding && trackWidthPx > 0) {
            SliderTooltip(
                currentMs = localPosition,
                durationMs = safeDuration,
                trackWidthPx = trackWidthPx,
                thumbSize = thumbSize,
                modifier = Modifier.align(Alignment.TopStart),
            )
        }

        // Shadow slider to make accurate tooltip position
        SliderGestureLayer(
            value = localPosition,
            duration = safeDuration,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun SliderTrackWithThumb(
    localPosition: Long,
    localBuffered: Long,
    safeDuration: Long,
    opening: SkipRange?,
    ending: SkipRange?,
    trackWidthPx: Int,
    trackHeight: Dp,
    thumbSize: Dp,
    onTrackWidthMeasured: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    val fraction = (localPosition.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .onGloballyPositioned { coordinates ->
                onTrackWidthMeasured(coordinates.size.width)
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .sliderTrackOverlay(
                    durationMs = safeDuration,
                    currentMs = localPosition,
                    bufferedMs = localBuffered,
                    opening = opening,
                    ending = ending,
                )
        )

        if (trackWidthPx > 0) {
            val thumbSizePx = with(density) { thumbSize.roundToPx() }
            val thumbLeftX = ((trackWidthPx - thumbSizePx) * fraction).roundToInt()

            Box(
                modifier = Modifier
                    .offset { IntOffset(x = thumbLeftX, y = 0) }
                    .size(thumbSize)
                    .shadow(
                        elevation = 2.dp,
                        shape = CircleShape,
                        clip = false,
                    )
                    .background(
                        color = SliderThumbColor,
                        shape = CircleShape,
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SliderGestureLayer(
    value: Long,
    duration: Long,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Slider(
        value = value.toFloat(),
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = 0f..duration.toFloat(),
        modifier = modifier.fillMaxWidth(),
        thumb = {
            Box(modifier = Modifier.size(1.dp))
        },
        track = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
        },
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent,
        )
    )
}