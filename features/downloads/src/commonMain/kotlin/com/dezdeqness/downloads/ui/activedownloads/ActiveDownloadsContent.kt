package com.dezdeqness.downloads.ui.activedownloads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ActiveDownloadsContent(
    state: ActiveDownloadsState,
    onBackPressed: () -> Unit,
    onDeleteClicked: (Long) -> Unit,
    onRetryClicked: (Long) -> Unit,
    onCancelClicked: (Long) -> Unit,
    onPauseClicked: (Long) -> Unit,
    onHideFromHistoryClicked: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            AppToolbar(
                title = { Text("Загрузки") },
                navigation = {
                    AppIconButton(
                        onClick = onBackPressed,
                        contentColor = AppTheme.colors.background,
                    ) {
                        Icon(
                            imageVector = AkaneIcons.Back,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.background,
                    titleContentColor = AppTheme.colors.textPrimary,
                ),
            )
        },
        containerColor = AppTheme.colors.background,
    ) { padding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(padding).fillMaxSize(),
        ) {
            if (state.isEmptyState) {
                DownloadsEmptyState(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                DownloadsList(
                    modifier = Modifier.widthIn(max = 800.dp),
                    activeDownloads = state.activeDownloads,
                    historyDownloads = state.historyDownloads,
                    completedGroups = state.completedGroups,
                    onDeleteClicked = onDeleteClicked,
                    onRetryClicked = onRetryClicked,
                    onCancelClicked = onCancelClicked,
                    onPauseClicked = onPauseClicked,
                    onHideFromHistoryClicked = onHideFromHistoryClicked,
                )
            }
        }
    }
}
