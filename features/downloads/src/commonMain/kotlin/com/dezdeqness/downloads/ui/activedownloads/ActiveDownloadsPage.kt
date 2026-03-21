package com.dezdeqness.downloads.ui.activedownloads

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveDownloadsPage(
    onBackPressed: () -> Unit,
    viewModel: ActiveDownloadsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    val isEmpty = state.activeDownloads.isEmpty()
            && state.historyDownloads.isEmpty()
            && state.completedDownloads.isEmpty()

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
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(padding)

        if (isEmpty) {
            DownloadsEmptyState(modifier = contentModifier)
        } else {
            DownloadsList(
                activeDownloads = state.activeDownloads,
                historyDownloads = state.historyDownloads,
                completedDownloads = state.completedDownloads,
                onDeleteClicked = viewModel::onDeleteClicked,
                onRetryClicked = viewModel::onRetryClicked,
                onCancelClicked = viewModel::onCancelClicked,
                onPauseClicked = viewModel::onPauseClicked,
                onHideFromHistoryClicked = viewModel::onHideFromHistoryClicked,
                modifier = contentModifier,
            )
        }
    }
}
