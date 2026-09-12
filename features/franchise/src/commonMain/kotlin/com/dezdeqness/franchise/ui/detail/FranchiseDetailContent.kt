package com.dezdeqness.franchise.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.franchise.ui.composables.FranchiseDetailLoading
import com.dezdeqness.franchise.ui.composables.FranchiseDetailToolbar
import com.dezdeqness.franchise.ui.composables.FranchiseError

@Composable
fun FranchiseDetailContent(
    state: FranchiseDetailState,
    franchiseName: String,
    onBackPressed: () -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        when (state.status) {
            FranchiseDetailStatus.Loading -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    FranchiseDetailToolbar(
                        title = franchiseName,
                        onBackPressed = onBackPressed,
                    )
                    AdaptiveLayout(modifier = Modifier.fillMaxSize()) {
                        val columns = if (LocalLayoutType.current == LayoutType.Mobile) 3 else 4
                        FranchiseDetailLoading(columns = columns)
                    }
                }
            }

            FranchiseDetailStatus.Error -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    FranchiseDetailToolbar(
                        title = franchiseName,
                        onBackPressed = onBackPressed,
                    )
                    FranchiseError(
                        modifier = Modifier.fillMaxSize(),
                        onAction = onRetryClicked,
                    )
                }
            }

            FranchiseDetailStatus.Loaded,
            FranchiseDetailStatus.Empty -> {
                FranchiseDetailLoaded(
                    header = state.header,
                    items = state.items,
                    franchiseName = franchiseName,
                    onBackPressed = onBackPressed,
                    onReleaseClicked = onReleaseClicked,
                )
            }
        }
    }
}
