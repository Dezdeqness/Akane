package com.dezdeqness.stories.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.designsystem.icons.AkaneIcons

@Composable
internal fun StoryHeader(
    title: String,
    story: IndexedStory,
    isMuted: Boolean,
    onMuteClicked: () -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = story,
            contentKey = { it.story.episodeId },
            transitionSpec = { storySwapTransition() },
            modifier = Modifier.weight(1f).clipToBounds(),
            label = "storyHeader",
        ) { (_, shown) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppImage(
                    data = shown.poster,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.75f),
                    )
                    Text(
                        text = shown.episodeLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        IconButton(onClick = onMuteClicked) {
            Icon(
                imageVector = if (isMuted) AkaneIcons.VolumeOff else AkaneIcons.VolumeUp,
                contentDescription = null,
                tint = Color.White,
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                imageVector = AkaneIcons.Close,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}
