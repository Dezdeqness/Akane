package com.dezdeqness.details.ui.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseToolbarInitial(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {

    AppToolbar(
        modifier = modifier,
        title = {},
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
    )
}
