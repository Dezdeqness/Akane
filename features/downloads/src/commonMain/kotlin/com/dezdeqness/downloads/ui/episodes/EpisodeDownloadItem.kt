package com.dezdeqness.downloads.ui.episodes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.downloads.ui.model.DownloadUiModel
import androidx.compose.material3.Text

@Composable
internal fun EpisodeDownloadItem(
    episode: DownloadUiModel,
    onPlayClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPlayClicked)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppImage(
            data = episode.previewUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp, 60.dp)
                .clip(RoundedCornerShape(4.dp)),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = "${episode.episodeOrdinal} эпизод — ${episode.episodeName}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = episode.quality,
                fontSize = 12.sp,
                color = AppTheme.colors.textSecondary,
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
        ) {
            AppIconButton(onClick = onPlayClicked, contentColor = AppTheme.colors.background) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = AppTheme.colors.textSecondary,
                )
            }
            AppIconButton(onClick = onDeleteClicked, contentColor = AppTheme.colors.background) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = AppTheme.colors.textSecondary,
                )
            }
        }
    }
}
