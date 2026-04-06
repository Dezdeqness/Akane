package com.dezdeqness.videoplayer.core.player.composables.slider

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.dezdeqness.videoplayer.core.player.data.SkipRange

private val SliderActiveTrackColor = Color.White
private val SliderInactiveTrackColor = Color.White.copy(alpha = 0.18f)
private val SliderBufferedTrackColor = Color.White.copy(alpha = 0.35f)
private val SliderSkipTickColor = Color.White.copy(alpha = 0.85f)
private val SliderSkipBorderColor = Color.Black.copy(alpha = 0.35f)

fun Modifier.sliderTrackOverlay(
    durationMs: Long,
    currentMs: Long,
    bufferedMs: Long,
    opening: SkipRange?,
    ending: SkipRange?,
): Modifier = composed {
    drawWithContent {
        drawContent()

        if (durationMs <= 0L) return@drawWithContent

        val width = size.width
        val height = size.height
        val radius = CornerRadius(height / 2f, height / 2f)

        val currentFraction = (currentMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        val bufferedFraction = (bufferedMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

        drawBaseTrack(width, height, radius)
        drawBufferedTrack(width, height, radius, bufferedFraction)
        drawSkipRanges(opening, ending, durationMs, width, height)
        drawCurrentTrack(width, height, radius, currentFraction)
    }
}

fun DrawScope.drawBaseTrack(
    width: Float,
    height: Float,
    radius: CornerRadius,
) {
    drawRoundRect(
        color = SliderInactiveTrackColor,
        topLeft = Offset.Zero,
        size = Size(width, height),
        cornerRadius = radius,
    )
}

fun DrawScope.drawBufferedTrack(
    width: Float,
    height: Float,
    radius: CornerRadius,
    bufferedFraction: Float,
) {
    drawRoundRect(
        color = SliderBufferedTrackColor,
        topLeft = Offset.Zero,
        size = Size(width * bufferedFraction, height),
        cornerRadius = radius,
    )
}

fun DrawScope.drawCurrentTrack(
    width: Float,
    height: Float,
    radius: CornerRadius,
    currentFraction: Float,
) {
    drawRoundRect(
        color = SliderActiveTrackColor,
        topLeft = Offset.Zero,
        size = Size(width * currentFraction, height),
        cornerRadius = radius,
    )
}

fun DrawScope.drawSkipRanges(
    opening: SkipRange?,
    ending: SkipRange?,
    durationMs: Long,
    trackWidth: Float,
    trackHeight: Float,
) {
    drawSkipTicksRange(
        range = opening,
        durationMs = durationMs,
        trackWidth = trackWidth,
        trackHeight = trackHeight,
    )
    drawSkipTicksRange(
        range = ending,
        durationMs = durationMs,
        trackWidth = trackWidth,
        trackHeight = trackHeight,
    )
}

fun DrawScope.drawSkipTicksRange(
    range: SkipRange?,
    durationMs: Long,
    trackWidth: Float,
    trackHeight: Float,
) {
    if (range == null || durationMs <= 0L) return
    if (range.endMs <= range.startMs) return

    val startFraction = (range.startMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    val endFraction = (range.endMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    val startX = trackWidth * startFraction
    val endX = trackWidth * endFraction
    val rangeWidth = endX - startX

    if (rangeWidth <= 0f) return

    drawSkipBorders(startX, endX, trackHeight)
    drawSkipTicks(startX, endX, trackHeight)
}

fun DrawScope.drawSkipBorders(
    startX: Float,
    endX: Float,
    trackHeight: Float,
) {
    val borderStroke = 1.5.dp.toPx()

    drawLine(
        color = SliderSkipBorderColor,
        start = Offset(startX, 0f),
        end = Offset(startX, trackHeight),
        strokeWidth = borderStroke,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = SliderSkipBorderColor,
        start = Offset(endX, 0f),
        end = Offset(endX, trackHeight),
        strokeWidth = borderStroke,
        cap = StrokeCap.Round,
    )
}

fun DrawScope.drawSkipTicks(
    startX: Float,
    endX: Float,
    trackHeight: Float,
) {
    val tickSpacing = 6.dp.toPx()
    val tickWidth = 1.dp.toPx()
    val tickTop = trackHeight * 0.22f
    val tickBottom = trackHeight * 0.78f

    var x = startX + tickSpacing / 2f
    while (x < endX) {
        drawLine(
            color = SliderSkipTickColor,
            start = Offset(x, tickTop),
            end = Offset(x, tickBottom),
            strokeWidth = tickWidth,
            cap = StrokeCap.Round,
        )
        x += tickSpacing
    }
}

fun formatDuration(totalSeconds: Long): String {
    val safe = totalSeconds.coerceAtLeast(0L)
    val hours = safe / 3600
    val minutes = (safe % 3600) / 60
    val seconds = safe % 60

    return if (hours > 0) {
        "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    } else {
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}