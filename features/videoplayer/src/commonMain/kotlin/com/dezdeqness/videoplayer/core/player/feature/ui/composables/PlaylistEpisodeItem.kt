package com.dezdeqness.videoplayer.core.player.feature.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.videoplayer.core.player.data.MediaItem
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun PlaylistEpisodeItem(
    item: MediaItem,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.45f else 0f,
        animationSpec = tween(250),
        label = "overlayAlpha"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelected)
            .padding(16.dp)
    ) {

        Box {
            AppImage(
                data = item.previewUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Color.Black.copy(alpha = overlayAlpha)
                    )
            )

            SelectedIndicator(
                isSelected = isSelected,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = "${item.ordinal} эпизод",
                fontSize = 12.sp,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
fun SelectedIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val bars = remember {
        List(3) { Animatable(0.2f) }
    }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            bars.forEachIndexed { index, animatable ->
                launch {
                    while (isActive) {
                        val target = when (index) {
                            0 -> Random.nextFloat().coerceIn(0.3f, 1f)
                            1 -> Random.nextFloat().coerceIn(0.5f, 1f)
                            else -> Random.nextFloat().coerceIn(0.2f, 0.8f)
                        }

                        animatable.animateTo(
                            targetValue = target,
                            animationSpec = tween(
                                durationMillis = 180,
                                easing = LinearEasing
                            )
                        )
                    }
                }
            }
        } else {
            bars.forEach {
                launch {
                    it.animateTo(
                        0.1f,
                        animationSpec = tween(200)
                    )
                }
            }
        }
    }
    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .height(28.dp)
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            bars.forEach { bar ->
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height((bar.value * 28).dp)
                        .clip(RoundedCornerShape(100))
                        .background(Color.White)
                )
            }
        }
    }
}
