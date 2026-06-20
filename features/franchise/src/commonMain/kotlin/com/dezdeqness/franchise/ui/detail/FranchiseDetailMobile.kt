package com.dezdeqness.franchise.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.dezdeqness.catalog.ui.ReleaseGrid
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.designsystem.nestedscroll.CollapsingAppBarNestedScrollConnection
import com.dezdeqness.designsystem.nestedscroll.ExpandedHeader
import com.dezdeqness.designsystem.nestedscroll.LocalNestedScrollConnection
import com.dezdeqness.franchise.ui.composables.FranchiseHeader
import com.dezdeqness.franchise.ui.composables.FranchiseToolbar
import com.dezdeqness.franchise.ui.model.FranchiseHeaderUiModel

@Composable
fun FranchiseDetailMobile(
    header: FranchiseHeaderUiModel?,
    items: List<ReleaseListUiModel>,
    franchiseName: String,
    onBackPressed: () -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val connection = remember { CollapsingAppBarNestedScrollConnection(scope) }

    CompositionLocalProvider(LocalNestedScrollConnection provides connection) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(connection),
        ) {
            Column(
                modifier = Modifier.scrollable(
                    orientation = Orientation.Vertical,
                    state = rememberScrollableState { 0f },
                ),
            ) {
                ExpandedHeader(
                    header = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationY = -connection.headerOffset * 0.5f
                                },
                        ) {
                            header?.let { FranchiseHeader(item = it) }
                        }
                    },
                    navigationBar = {
                        FranchiseToolbar(
                            title = franchiseName,
                            onBackPressed = onBackPressed,
                        )
                    },
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(AppTheme.colors.background),
                ) {
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
                            columnCount = 3,
                            contentPadding = PaddingValues(8.dp),
                            onLoadMore = {},
                            onReleaseClicked = onReleaseClicked,
                        )
                    }
                }
            }
        }
    }
}
