package com.dezdeqness.franchise.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FranchiseDetailPage(
    franchiseName: String,
    onBackPressed: () -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
    viewModel: FranchiseDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    FranchiseDetailContent(
        state = state,
        franchiseName = franchiseName,
        onBackPressed = onBackPressed,
        onReleaseClicked = onReleaseClicked,
        onRetryClicked = viewModel::onRetryClicked,
    )
}
