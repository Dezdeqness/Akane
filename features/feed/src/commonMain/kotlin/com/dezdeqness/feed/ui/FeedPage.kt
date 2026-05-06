package com.dezdeqness.feed.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
    onReleaseClicked: (Long, String) -> Unit,
) {
    val state by viewModel.feedStateFlow.collectAsStateOnLifecycle()
    val isFeedFilterShownState by viewModel.isFeedFilterShownState.collectAsStateOnLifecycle()

    AdaptiveLayout(modifier = modifier.fillMaxSize()) {
        when (val type = LocalLayoutType.current) {
            LayoutType.Mobile -> {
                FeedPageMobile(
                    state = state,
                    isFilterVisible = isFeedFilterShownState,
                    onQueryChanged = viewModel::onQueryChanged,
                    onFilterClicked = viewModel::onFilterClicked,
                    onRetryClicked = viewModel::onRetryClicked,
                    onFilterClosed = viewModel::onFilterClosed,
                    onFilterChanged = viewModel::onFilterChanged,
                    onLoadMore = viewModel::onLoadMore,
                    onReleaseClicked = { id, title ->
                        onReleaseClicked(id, title)
                    },
                )
            }

            LayoutType.Tablet,
            LayoutType.Desktop -> {
                FeedPageWide(
                    state = state,
                    isFilterVisible = isFeedFilterShownState,
                    useSidePanelFilter = type == LayoutType.Desktop,
                    onQueryChanged = viewModel::onQueryChanged,
                    onFilterClicked = viewModel::onFilterClicked,
                    onRetryClicked = viewModel::onRetryClicked,
                    onFilterClosed = viewModel::onFilterClosed,
                    onFilterChanged = viewModel::onFilterChanged,
                    onLoadMore = viewModel::onLoadMore,
                    onReleaseClicked = { id, title ->
                        onReleaseClicked(id, title)
                    },
                )
            }
        }
    }
}
