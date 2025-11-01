package com.dezdeqness.details.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseToolbar(
    modifier: Modifier = Modifier,
    title: String,
    isFavourite: Boolean,
    onBackPressed: () -> Unit,
    onFavouriteClicked: () -> Unit,
) {
    AppToolbar(
        modifier = modifier,
        title = {
            Text(
                title,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        },
        navigation = {
            AppIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBackPressed,
            )
        },
        colors = TopAppBarDefaults
            .topAppBarColors()
            .copy(
                containerColor = Color.Transparent,
            ),
        actions = {
            AppIconButton(
                icon = if (isFavourite) AkaneIcons.Favorite else AkaneIcons.FavoriteBorder,
                onClick = onFavouriteClicked,
                tint = AppTheme.colors.textPrimary,
            )
        }
    )
}
