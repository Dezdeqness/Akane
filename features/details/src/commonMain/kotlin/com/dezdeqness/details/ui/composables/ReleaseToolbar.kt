package com.dezdeqness.details.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.designsystem.nestedscroll.LocalNestedScrollConnection
import com.dezdeqness.details.ui.model.FavouriteButtonState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseToolbar(
    modifier: Modifier = Modifier,
    title: String,
    favouriteButtonState: FavouriteButtonState,
    onBackPressed: () -> Unit,
    onFavouriteClicked: () -> Unit,
) {
    val connection = LocalNestedScrollConnection.current
    val isScrollPass = remember {
        derivedStateOf {
            connection.progress > 0.7f
        }
    }

    AppToolbar(
        modifier = modifier,
        title = {
            if (isScrollPass.value.not()) {
                Text(
                    title,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigation = {
            AppIconButton(
                onClick = onBackPressed,
                contentColor = AppTheme.colors.surface,
            ) {
                Icon(
                    AkaneIcons.Back,
                    contentDescription = null,
                    tint = AppTheme.colors.textPrimary
                )
            }
        },
        colors = TopAppBarDefaults
            .topAppBarColors()
            .copy(
                containerColor = Color.Transparent,
            ),
        actions = {
            if (favouriteButtonState != FavouriteButtonState.Hidden) {
                AppIconButton(
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
                                tint = AppTheme.colors.textPrimary
                            )
                        }

                        FavouriteButtonState.Hidden -> Unit
                    }
                }
            }
        }
    )
}
