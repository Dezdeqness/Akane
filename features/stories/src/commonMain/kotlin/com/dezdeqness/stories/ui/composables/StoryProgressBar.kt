package com.dezdeqness.stories.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dezdeqness.stories.ui.playback.StoryPlayback
import com.dezdeqness.stories.ui.playback.StoryPlaybackController

@Composable
internal fun StoryProgressBar(
    count: Int,
    currentIndex: Int,
    currentProgress: Float,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(count) { index ->
            val progress = when {
                index < currentIndex -> 1f
                index == currentIndex -> currentProgress
                else -> 0f
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.35f)),
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(Color.White),
                )
            }
        }
    }
}

@Composable
internal fun rememberSmoothLoopProgress(playback: StoryPlayback): Float {
    var progress by remember { mutableFloatStateOf(playback.positionMs.toLoopProgress()) }

    LaunchedEffect(playback) {
        val reportedAt = withFrameMillis { it }
        val reported = playback.positionMs.toLoopProgress()
        val isJitter = reported < progress && progress - reported < 1_000f / StoryPlaybackController.LOOP_DURATION_MS
        if (!isJitter) progress = reported
        if (!playback.isPlaying) return@LaunchedEffect

        while (true) {
            val elapsed = withFrameMillis { it } - reportedAt
            progress = maxOf(progress, (playback.positionMs + elapsed).toLoopProgress())
        }
    }
    return progress
}

private fun Long.toLoopProgress() = (toFloat() / StoryPlaybackController.LOOP_DURATION_MS).coerceIn(0f, 1f)
