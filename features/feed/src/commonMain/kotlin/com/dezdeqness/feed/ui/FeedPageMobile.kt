package com.dezdeqness.feed.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.feed.contract.model.CatalogFilter
import com.dezdeqness.feed.ui.composable.FeedSearch
import com.dezdeqness.feed.ui.filter.composables.FeedFilterBottomSheet

@Composable
fun FeedPageMobile(
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
    Scaffold(
        containerColor = AppTheme.colors.background,
        modifier = modifier.fillMaxSize(),
        topBar = {
            FeedSearch(
                onQueryChanged = onQueryChanged,
                onFilterClicked = onFilterClicked,
            )
        }
    ) { contentPadding ->
        FeedContent(
            state = state,
            contentPadding = contentPadding,
            columns = 3,
            onRetryClicked = onRetryClicked,
            onFilterClicked = onFilterClicked,
            onLoadMore = onLoadMore,
            onReleaseClicked = onReleaseClicked,
        )

        if (isFilterVisible) {
            FeedFilterBottomSheet(
                catalogFilter = state.input.filterCatalogFilter,
                onClosed = onFilterClosed,
                onFilterChanged = onFilterChanged,
            )
        }
    }
}
