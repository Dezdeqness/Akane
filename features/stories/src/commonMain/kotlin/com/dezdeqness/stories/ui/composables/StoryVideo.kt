package com.dezdeqness.stories.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.stories.ui.model.StoryUiModel
import com.dezdeqness.videoplayer.core.player.CompositedVideoSurface
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager

@Composable
internal fun StoryVideo(
    story: StoryUiModel,
    player: VideoPlayerManager?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(VIDEO_ASPECT_RATIO)
            .clickable(onClick = onClick),
    ) {
        Crossfade(
            targetState = story.previewUrl,
            animationSpec = tween(durationMillis = SWAP_DURATION_MS),
            modifier = Modifier.matchParentSize(),
            label = "storyPreview",
        ) { previewUrl ->
            AppImage(
                data = previewUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        if (player != null) {
            key(player) {
                CompositedVideoSurface(
                    engine = player,
                    modifier = Modifier.matchParentSize(),
                )
            }
        }
    }
}

private const val VIDEO_ASPECT_RATIO = 16f / 9f
