package com.dezdeqness.videoplayer.core.player.composables.slider

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private val TooltipWidth = 64.dp
private val TooltipArrowWidth = 10.dp
private val TooltipArrowHeight = 6.dp
private val TooltipVerticalSpacing = 8.dp

private val SliderTooltipContainerColor = Color.Black.copy(alpha = 0.92f)
private val SliderTooltipContentColor = Color.White

@Composable
fun SliderTooltip(
    currentMs: Long,
    durationMs: Long,
    trackWidthPx: Int,
    thumbSize: Dp,
    tooltipPadding: Dp = 24.dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val fraction = (currentMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    val thumbSizePx = with(density) { thumbSize.roundToPx() }
    val tooltipWidthPx = with(density) { TooltipWidth.roundToPx() }
    val arrowWidthPx = with(density) { TooltipArrowWidth.roundToPx() }

    val thumbLeftX = ((trackWidthPx - thumbSizePx) * fraction).roundToInt()
    val thumbCenterX = thumbLeftX + thumbSizePx / 2

    val tooltipX = (thumbCenterX - tooltipWidthPx / 2)
        .coerceIn(0, trackWidthPx - tooltipWidthPx)

    val arrowOffsetInsideTooltip = (thumbCenterX - tooltipX - arrowWidthPx / 2)
        .coerceIn(0, tooltipWidthPx - arrowWidthPx)

    val arrowHeightPx = with(density) { TooltipArrowHeight.roundToPx() }
    val spacingPx = with(density) { TooltipVerticalSpacing.roundToPx() }

    Box(
        modifier = modifier.offset {
            IntOffset(
                x = tooltipX,
                y = -(arrowHeightPx + spacingPx + with(density) { tooltipPadding.roundToPx() })
            )
        }
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            TooltipContent(currentMs)
            TooltipArrow(
                modifier = Modifier.offset {
                    IntOffset(
                        x = arrowOffsetInsideTooltip,
                        y = 0,
                    )
                }
            )
        }
    }
}

@Composable
private fun TooltipContent(
    currentMs: Long,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(TooltipWidth)
            .background(
                color = SliderTooltipContainerColor,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 8.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = formatDuration(currentMs / 1000),
            color = SliderTooltipContentColor,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun TooltipArrow(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.size(
            width = TooltipArrowWidth,
            height = TooltipArrowHeight,
        )
    ) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width / 2f, size.height)
            close()
        }
        drawPath(
            path = path,
            color = SliderTooltipContainerColor,
        )
    }
}
