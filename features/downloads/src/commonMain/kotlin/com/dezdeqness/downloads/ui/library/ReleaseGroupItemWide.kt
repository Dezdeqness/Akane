package com.dezdeqness.downloads.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.downloads.ui.model.ReleaseGroup

@Composable
fun ReleaseGroupItemWide(
    group: ReleaseGroup,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClicked),
    ) {
        AppImage(
            data = group.previewUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(8.dp)),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        ) {
            Text(
                text = group.releaseTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${group.totalSize} ${episodeCountText(group.totalSize)}",
                fontSize = 13.sp,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

private fun episodeCountText(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod100 in 11..19 -> "эпизодов"
        mod10 == 1 -> "эпизод"
        mod10 in 2..4 -> "эпизода"
        else -> "эпизодов"
    }
}
