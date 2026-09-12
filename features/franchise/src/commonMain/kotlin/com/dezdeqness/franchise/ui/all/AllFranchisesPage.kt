package com.dezdeqness.franchise.ui.all

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AllFranchisesPage(
    onBackPressed: () -> Unit,
    onFranchiseClicked: (franchiseId: String, franchiseName: String) -> Unit,
    viewModel: AllFranchisesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    AllFranchisesContent(
        state = state,
        onBackPressed = onBackPressed,
        onRetryClicked = viewModel::onRetryClicked,
        onFranchiseClicked = onFranchiseClicked,
    )
}
