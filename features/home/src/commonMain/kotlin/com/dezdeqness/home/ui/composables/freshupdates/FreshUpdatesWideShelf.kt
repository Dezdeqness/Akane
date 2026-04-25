package com.dezdeqness.home.ui.composables.freshupdates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.home.ui.model.HomeUiModel

@Composable
fun FreshUpdatesWideShelf(
    items: List<HomeUiModel>,
    selectedItemId: Long,
    height: Dp,
    width: Dp,
    onItemSelected: (HomeUiModel) -> Unit,
) {
    if (items.isEmpty()) return

    val listState = rememberLazyListState()

    LaunchedEffect(selectedItemId) {
        val index = items.indexOfFirst { it.id == selectedItemId }
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    Surface(
        modifier = Modifier
            .width(width)
            .height(height),
        shape = RoundedCornerShape(30.dp),
        color = AppTheme.colors.surface.copy(alpha = 0.72f),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(
                count = items.size,
                key = { index -> items[index].id}
            ) { index ->
                val item = items[index]
                FreshUpdatesWideShelfItem(
                    item = item,
                    selected = item.id == selectedItemId,
                    onClick = { onItemSelected(item) },
                )
            }
        }
    }
}

@Composable
private fun FreshUpdatesWideShelfItem(
    item: HomeUiModel,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    val borderColor = if (selected) {
        AppTheme.colors.primary
    } else {
        Color.Transparent
    }
    val backgroundColor = if (selected) {
        AppTheme.colors.surfaceVariant.copy(alpha = 0.92f)
    } else {
        AppTheme.colors.background.copy(alpha = 0.18f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppImage(
            data = item.imagePath,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 68.dp, height = 88.dp)
                .clip(RoundedCornerShape(16.dp)),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = item.name,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
