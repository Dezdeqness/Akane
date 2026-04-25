package com.dezdeqness.feed.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.feed.ui.model.FeedAnimeUiModel

internal const val FeedItemAspectRatio = 2 / 3f

@Composable
fun FeedItem(
    modifier: Modifier = Modifier,
    item: FeedAnimeUiModel,
    onReleaseClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(FeedItemAspectRatio)
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                onClick = {
                    onReleaseClicked.invoke()
                }
            )
    ) {
        AppImage(
            data = item.imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
