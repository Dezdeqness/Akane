package com.dezdeqness.feed.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.feed.contract.model.CatalogFilter

@Composable
fun FeedScreen(
    state: FeedState,
    isFilterVisible: Boolean,
    onQueryChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    onFilterClosed: () -> Unit,
    onFilterChanged: (CatalogFilter) -> Unit,
    onLoadMore: () -> Unit,
    onReleaseClicked: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdaptiveLayout(modifier = modifier.fillMaxSize()) {
        when (val type = LocalLayoutType.current) {
            LayoutType.Mobile -> {
                FeedPageMobile(
                    state = state,
                    isFilterVisible = isFilterVisible,
                    onQueryChanged = onQueryChanged,
                    onFilterClicked = onFilterClicked,
                    onRetryClicked = onRetryClicked,
                    onFilterClosed = onFilterClosed,
                    onFilterChanged = onFilterChanged,
                    onLoadMore = onLoadMore,
                    onReleaseClicked = onReleaseClicked,
                )
            }

            LayoutType.Tablet,
            LayoutType.Desktop -> {
                FeedPageWide(
                    state = state,
                    isFilterVisible = isFilterVisible,
                    useSidePanelFilter = type == LayoutType.Desktop,
                    onQueryChanged = onQueryChanged,
                    onFilterClicked = onFilterClicked,
                    onRetryClicked = onRetryClicked,
                    onFilterClosed = onFilterClosed,
                    onFilterChanged = onFilterChanged,
                    onLoadMore = onLoadMore,
                    onReleaseClicked = onReleaseClicked,
                )
            }
        }
    }
}
