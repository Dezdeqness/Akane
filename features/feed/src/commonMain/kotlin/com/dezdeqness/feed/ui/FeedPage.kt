package com.dezdeqness.feed.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    FeedScreen(
        state = state,
        isFilterVisible = isFeedFilterShownState,
        onQueryChanged = viewModel::onQueryChanged,
        onFilterClicked = viewModel::onFilterClicked,
        onRetryClicked = viewModel::onRetryClicked,
        onFilterClosed = viewModel::onFilterClosed,
        onFilterChanged = viewModel::onFilterChanged,
        onLoadMore = viewModel::onLoadMore,
        onReleaseClicked = onReleaseClicked,
        modifier = modifier,
    )
}
