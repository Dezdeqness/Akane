package com.dezdeqness.details.ui.composables.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.details.ui.model.FavouriteButtonState
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel

@Composable
fun ReleaseDetailsWideSidebar(
    modifier: Modifier = Modifier,
    details: ReleaseDetailsUiModel,
    favouriteButtonState: FavouriteButtonState,
    onFavouriteClicked: () -> Unit,
) {
    val header = details.header

    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box {
                AppImage(
                    data = header.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(2 / 3f)
                        .clip(RoundedCornerShape(16.dp)),
                )

                if (favouriteButtonState != FavouriteButtonState.Hidden) {
                    AppIconButton(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                        onClick = {
                            if (favouriteButtonState !is FavouriteButtonState.Loading) onFavouriteClicked()
                        },
                        contentColor = AppTheme.colors.surface,
                    ) {
                        when (favouriteButtonState) {
                            FavouriteButtonState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = AppTheme.colors.textPrimary,
                                )
                            }

                            is FavouriteButtonState.Loaded -> {
                                Icon(
                                    if (favouriteButtonState.isFavourite) AkaneIcons.Favorite else AkaneIcons.FavoriteBorder,
                                    contentDescription = null,
                                    tint = AppTheme.colors.textPrimary,
                                )
                            }

                            FavouriteButtonState.Hidden -> Unit
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = header.title,
                    style = AppTheme.typography.titleMedium.copy(fontSize = 24.sp),
                    color = AppTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                val metadata = remember(header.season, header.year) {
                    listOf(header.season, header.year)
                        .filter { it.isNotBlank() }
                        .joinToString(" • ")
                }

                if (metadata.isNotBlank()) {
                    Text(
                        text = metadata,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}
