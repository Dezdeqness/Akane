package com.dezdeqness.home.ui.composables.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.home.ui.model.StoryCollectionPreviewUiModel

@Composable
fun StoriesRail(
    items: List<StoryCollectionPreviewUiModel>,
    onItemClicked: (StoryCollectionPreviewUiModel) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    circleSize: Dp = 68.dp,
) {
    if (items.isEmpty()) return

    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(items, key = { it.index }) { item ->
            StoryCircle(
                item = item,
                circleSize = circleSize,
                onClick = { onItemClicked(item) },
            )
        }
    }
}

@Composable
private fun StoryCircle(
    item: StoryCollectionPreviewUiModel,
    circleSize: Dp,
    onClick: () -> Unit,
) {
    val ring = Brush.linearGradient(
        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary),
    )

    Column(
        modifier = Modifier
            .width(circleSize + 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box {
            AppImage(
                data = item.coverUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(circleSize)
                    .border(width = 2.5.dp, brush = ring, shape = CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = item.storiesCount.toString(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 6.dp, vertical = 1.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
