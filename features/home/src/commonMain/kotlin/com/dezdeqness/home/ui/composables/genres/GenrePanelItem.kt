package com.dezdeqness.home.ui.composables.genres

import akane.features.home.generated.resources.Res
import akane.features.home.generated.resources.releases_count
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import org.jetbrains.compose.resources.pluralStringResource

private const val GenrePosterAspectRatio = 2 / 3f

@Composable
fun GenrePanelItem(
    modifier: Modifier = Modifier,
    item: GenrePanelUiModel,
    cardWidth: Dp,
    titleStyle: TextStyle,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(cardWidth)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AppImage(
            data = item.imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(GenrePosterAspectRatio)
                .clip(RoundedCornerShape(12.dp)),
        )
        Text(
            text = item.name,
            style = titleStyle,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        Text(
            text = pluralStringResource(
                Res.plurals.releases_count,
                item.totalReleases,
                item.totalReleases,
            ),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}

@Composable
internal fun GenresSeeAll(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Все",
        style = AppTheme.typography.labelMedium,
        color = AppTheme.colors.textSecondary,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
