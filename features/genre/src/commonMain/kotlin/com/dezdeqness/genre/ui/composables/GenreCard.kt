package com.dezdeqness.genre.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import akane.features.genre.generated.resources.Res
import akane.features.genre.generated.resources.releases_count
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.genre.ui.model.GenreUiModel
import org.jetbrains.compose.resources.pluralStringResource

@Composable
fun GenreCard(
    modifier: Modifier = Modifier,
    item: GenreUiModel,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        AppImage(
            data = item.imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .aspectRatio(2 / 3f),
        )

        Text(
            text = item.name,
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
        )
    }
}
