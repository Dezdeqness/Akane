package com.dezdeqness.videoplayer.core.player

import akane.features.videoplayer.generated.resources.Res
import akane.features.videoplayer.generated.resources.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun VideoPlayerControls(
    context: PlayerContext,
    modifier: Modifier = Modifier,
) {
    val state by context.playerState.collectAsStateOnLifecycle()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SeekButton(
            iconRes = Res.drawable.ic_time_backward,
            label = "10",
            onClick = context::seekBack,
        )

        PlayPauseButton(
            isPlaying = state.isPlaying,
            isBuffering = state.isBuffering,
            onClick = {
                if (state.isPlaying) context.pause() else context.play()
            },
        )

        SeekButton(
            iconRes = Res.drawable.ic_time_forward,
            label = "10",
            onClick = context::seekForward,
        )
    }
}

@Composable
private fun SeekButton(
    iconRes: DrawableResource,
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 10.sp)
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    isBuffering: Boolean,
    onClick: () -> Unit,
) {
    AnimatedContent(
        targetState = isBuffering,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        label = "PlayPauseTransition",
    ) { buffering ->
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (buffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.White,
                    trackColor = Color.Transparent,
                    strokeWidth = 3.dp,
                )
            } else {
                Icon(
                    painter = painterResource(if (isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
