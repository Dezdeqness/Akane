package com.dezdeqness.stories.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.stories.ui.favourite.FavouriteState

@Composable
internal fun StoryCtaBanner(
    story: IndexedStory,
    favourite: FavouriteState,
    onFavouriteClicked: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White.copy(alpha = 0.14f))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = story,
            contentKey = { it.story.releaseId },
            transitionSpec = { storySwapTransition() },
            modifier = Modifier.weight(1f),
            label = "storyCta",
        ) { (_, shown) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppImage(
                    data = shown.poster,
                    contentDescription = null,
                    modifier = Modifier
                        .width(44.dp)
                        .height(62.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = shown.releaseName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    TextButton(
                        onClick = onClick,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(28.dp),
                    ) {
                        Text(
                            text = "Подробнее об аниме",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
        if (favourite is FavouriteState.Visible) {
            FavouriteButton(
                isFavourite = story.story.releaseId in favourite.favouriteIds,
                isLoading = story.story.releaseId in favourite.inProgressIds,
                onClick = onFavouriteClicked,
            )
        }
    }
}

@Composable
private fun FavouriteButton(
    isFavourite: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, enabled = !isLoading) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Color.White,
            )
        } else {
            Icon(
                imageVector = if (isFavourite) AkaneIcons.Favorite else AkaneIcons.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavourite) MaterialTheme.colorScheme.primary else Color.White,
            )
        }
    }
}
