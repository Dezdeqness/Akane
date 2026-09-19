package com.dezdeqness.auth.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType

@Composable
fun AuthFormScaffold(
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppTheme.colors.background,
        topBar = {
            if (onClose != null) {
                AppToolbar(
                    title = {},
                    navigation = {
                        AppIconButton(
                            onClick = onClose,
                        ) {
                            Icon(
                                imageVector = AkaneIcons.Close,
                                contentDescription = "Закрыть",
                                tint = AppTheme.colors.textPrimary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppTheme.colors.background,
                        titleContentColor = AppTheme.colors.textPrimary,
                    ),
                )
            }
        },
    ) { innerPadding ->
        AdaptiveLayout {
            val layoutType = LocalLayoutType.current

            val alignment = if (layoutType == LayoutType.Mobile) {
                Alignment.TopCenter
            } else {
                Alignment.Center
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = alignment,
            ) {
                when (layoutType) {
                    LayoutType.Mobile -> AuthFormBody(content = content)
                    LayoutType.Tablet -> AuthFormBody(formMaxWidth = 460.dp, content = content)
                    LayoutType.Desktop -> AuthFormBody(formMaxWidth = 520.dp, content = content)
                }
            }
        }
    }
}

@Composable
private fun AuthFormBody(
    formMaxWidth: Dp? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .let { if (formMaxWidth != null) it.widthIn(max = formMaxWidth) else it }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}
