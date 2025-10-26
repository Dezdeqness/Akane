package com.dezdeqness.feed.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.feed.ui.model.FeedAnimeUiModel

@Composable
fun FeedItem(
    modifier: Modifier = Modifier,
    item: FeedAnimeUiModel,
    onReleaseClicked: (Long) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                onClick = {
                    onReleaseClicked.invoke(item.id)
                }
            )
    ) {
        AppImage(
            data = item.imageUrl,
            modifier = Modifier.height(150.dp)
        )
    }
}
