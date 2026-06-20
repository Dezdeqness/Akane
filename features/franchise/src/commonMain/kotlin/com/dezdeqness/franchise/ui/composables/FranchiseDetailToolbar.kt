package com.dezdeqness.franchise.ui.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FranchiseDetailToolbar(
    title: String,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppToolbar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigation = {
            AppIconButton(
                onClick = onBackPressed,
                contentColor = AppTheme.colors.surface,
            ) {
                Icon(
                    imageVector = AkaneIcons.Back,
                    contentDescription = null,
                    tint = AppTheme.colors.textPrimary,
                )
            }
        },
        colors = TopAppBarDefaults
            .topAppBarColors()
            .copy(containerColor = AppTheme.colors.background),
    )
}
