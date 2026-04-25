package com.dezdeqness.feed.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.feed.domain.model.CatalogFilter
import com.dezdeqness.feed.ui.composable.FeedSearch
import com.dezdeqness.feed.ui.filter.composables.FeedFilterBottomSheet
import com.dezdeqness.feed.ui.filter.composables.FeedFilterSidePanel

private val FeedFilterPanelWidth = 360.dp
private val FeedContentPanelSpacing = 24.dp

@Composable
fun FeedPageWide(
    state: FeedState,
    isFilterVisible: Boolean,
    useSidePanelFilter: Boolean,
    onQueryChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    onFilterClosed: () -> Unit,
    onFilterChanged: (CatalogFilter) -> Unit,
    onLoadMore: () -> Unit,
    onReleaseClicked: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val horizontalPadding = when {
            maxWidth >= 1400.dp -> 32.dp
            else -> 24.dp
        }
        Scaffold(
            containerColor = AppTheme.colors.background,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                FeedSearch(
                    onQueryChanged = onQueryChanged,
                    showFilterButton = !useSidePanelFilter,
                    onFilterClicked = onFilterClicked,
                )
            }
        ) { contentPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
            ) {
                val feedContentWidth =
                    maxWidth - (horizontalPadding * 2) - if (useSidePanelFilter) {
                        FeedFilterPanelWidth + FeedContentPanelSpacing
                    } else {
                        0.dp
                    }
                val feedColumns = when {
                    feedContentWidth >= 1500.dp -> 7
                    feedContentWidth >= 1250.dp -> 6
                    feedContentWidth >= 1000.dp -> 5
                    feedContentWidth >= 760.dp -> 4
                    else -> 3
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding, vertical = 16.dp)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(FeedContentPanelSpacing),
                ) {
                    FeedContent(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        state = state,
                        columns = feedColumns,
                        onRetryClicked = onRetryClicked,
                        onFilterClicked = onFilterClicked,
                        onLoadMore = onLoadMore,
                        onReleaseClicked = onReleaseClicked,
                    )

                    if (useSidePanelFilter) {
                        Box(
                            modifier = Modifier
                                .width(FeedFilterPanelWidth)
                                .fillMaxHeight(),
                        ) {
                            FeedFilterSidePanel(
                                modifier = Modifier.fillMaxSize(),
                                catalogFilter = state.input.filterCatalogFilter,
                                closeOnApply = false,
                                onClosed = onFilterClosed,
                                onFilterChanged = onFilterChanged,
                            )
                        }
                    }
                }

                if (!useSidePanelFilter && isFilterVisible) {
                    FeedFilterBottomSheet(
                        catalogFilter = state.input.filterCatalogFilter,
                        onClosed = onFilterClosed,
                        onFilterChanged = onFilterChanged,
                    )
                }
            }
        }
    }
}
