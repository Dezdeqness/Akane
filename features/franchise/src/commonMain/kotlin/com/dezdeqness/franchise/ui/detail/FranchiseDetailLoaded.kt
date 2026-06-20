package com.dezdeqness.franchise.ui.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.franchise.ui.model.FranchiseHeaderUiModel

@Composable
fun FranchiseDetailLoaded(
    header: FranchiseHeaderUiModel?,
    items: List<ReleaseListUiModel>,
    franchiseName: String,
    onBackPressed: () -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdaptiveLayout(modifier = modifier.fillMaxSize()) {
        when (LocalLayoutType.current) {
            LayoutType.Mobile -> FranchiseDetailMobile(
                header = header,
                items = items,
                franchiseName = franchiseName,
                onBackPressed = onBackPressed,
                onReleaseClicked = onReleaseClicked,
            )

            else -> FranchiseDetailWide(
                header = header,
                items = items,
                franchiseName = franchiseName,
                onBackPressed = onBackPressed,
                onReleaseClicked = onReleaseClicked,
            )
        }
    }
}
