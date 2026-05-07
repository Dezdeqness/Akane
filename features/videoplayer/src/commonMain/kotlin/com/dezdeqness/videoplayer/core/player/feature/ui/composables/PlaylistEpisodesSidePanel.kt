package com.dezdeqness.videoplayer.core.player.feature.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.videoplayer.core.player.data.MediaItem

@Composable
fun PlaylistEpisodesSidePanel(
    items: List<MediaItem>,
    currentItemId: String,
    sheetVisible: Boolean,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val targetWidth = if (sheetVisible) 420.dp else 0.dp

    val width by animateDpAsState(
        targetValue = targetWidth,
        label = "side_panel_width"
    )

    AnimatedVisibility(
        visible = sheetVisible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth }
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth }
        ) + fadeOut(),
    ) {
        Column(
            modifier = modifier
                .width(width)
                .fillMaxHeight()
                .background(Color.Black)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Список серий",
                    modifier = Modifier.padding(16.dp)
                )

                AppIconButton(onClick = onDismiss, contentColor = Color.Transparent) {
                    Icon(
                        AkaneIcons.Close,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                    )
                }
            }
            HorizontalDivider()

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(items, key = MediaItem::id) { item ->
                    PlaylistEpisodeItem(
                        item = item,
                        isSelected = item.id == currentItemId,
                        onSelected = {
                            onSelected(item.id)
                        }
                    )
                }
            }
        }
    }
}
