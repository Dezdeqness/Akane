package com.dezdeqness.downloads.ui.activedownloads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ActiveDownloadsPage(
    onBackPressed: () -> Unit,
    viewModel: ActiveDownloadsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    ActiveDownloadsContent(
        state = state,
        onBackPressed = onBackPressed,
        onDeleteClicked = viewModel::onDeleteClicked,
        onRetryClicked = viewModel::onRetryClicked,
        onCancelClicked = viewModel::onCancelClicked,
        onPauseClicked = viewModel::onPauseClicked,
        onHideFromHistoryClicked = viewModel::onHideFromHistoryClicked,
    )
}
