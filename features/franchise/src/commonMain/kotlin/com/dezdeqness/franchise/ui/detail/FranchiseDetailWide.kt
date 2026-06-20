package com.dezdeqness.franchise.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.catalog.ui.ReleaseGrid
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.franchise.ui.composables.FranchiseDetailToolbar
import com.dezdeqness.franchise.ui.composables.FranchiseWideSidebar
import com.dezdeqness.franchise.ui.model.FranchiseHeaderUiModel

private val SidebarWidth = 360.dp

@Composable
fun FranchiseDetailWide(
    header: FranchiseHeaderUiModel?,
    items: List<ReleaseListUiModel>,
    franchiseName: String,
    onBackPressed: () -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppTheme.colors.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FranchiseDetailToolbar(
                title = header?.name ?: franchiseName,
                onBackPressed = onBackPressed,
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                header?.let {
                    FranchiseWideSidebar(
                        item = it,
                        modifier = Modifier.width(SidebarWidth),
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    if (items.isEmpty()) {
                        Text(
                            text = "В этой франшизе пока нет релизов",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textSecondary,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        ReleaseGrid(
                            list = items,
                            hasNextPage = false,
                            isPageLoading = false,
                            columnCount = 4,
                            contentPadding = PaddingValues(vertical = 8.dp),
                            onLoadMore = {},
                            onReleaseClicked = onReleaseClicked,
                        )
                    }
                }
            }
        }
    }
}
