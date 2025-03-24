package com.dezdeqness.videoplayer

import akane.features.videoplayer.generated.resources.Res
import akane.features.videoplayer.generated.resources.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource

@Composable
fun ControlPlayerView(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isLoading: Boolean,
    onSeekBackward: () -> Unit = {},
    onSeekForward: () -> Unit = {},
    onPlayPauseToggle: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(0.5f))
                .clickable {
                    onSeekBackward()
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "10",
                color = Color.White,
                fontSize = 10.sp,
            )
            Icon(
                painter = painterResource(Res.drawable.ic_time_backward),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color.Gray,
                trackColor = Color.Transparent,
            )
        } else {
            AnimatedContent(
                targetState = isPlaying,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "IconTransition"
            ) { playing ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(0.5f))
                        .clickable {
                            onPlayPauseToggle(playing)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(if (playing) Res.drawable.ic_pause else Res.drawable.ic_play),
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(0.5f))
                .clickable {
                    onSeekForward()
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "10",
                color = Color.White,
                fontSize = 10.sp,
            )
            Icon(
                painter = painterResource(Res.drawable.ic_time_forward),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
