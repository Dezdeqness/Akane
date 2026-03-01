package com.dezdeqness.feed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.feed.ui.composable.FeedEmpty
import com.dezdeqness.feed.ui.composable.FeedError
import com.dezdeqness.feed.ui.composable.FeedGrid
import com.dezdeqness.feed.ui.composable.FeedLoading
import com.dezdeqness.feed.ui.composable.FeedSearch
import com.dezdeqness.feed.ui.filter.composables.FeedFilterBottomSheet
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
    onReleaseClicked: (Long) -> Unit,
) {
    val state by viewModel.feedStateFlow.collectAsStateOnLifecycle()
    val isFeedFilterShownState by viewModel.isFeedFilterShownState.collectAsStateOnLifecycle()

    val hasNextPage = state.hasNextPage

    Scaffold(
        containerColor = AppTheme.colors.background,
        modifier = modifier.fillMaxSize(),
        topBar = {
            FeedSearch(
                onQueryChanged = viewModel::onQueryChanged,
                onFilterClicked = viewModel::onFilterClicked,
            )
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding).fillMaxSize()) {
            when (state.status) {
                Status.Initial, Status.Loading -> {
                    FeedLoading(Modifier.fillMaxSize())
                }

                Status.Error -> {
                    FeedError(
                        modifier = Modifier.align(Alignment.Center),
                        onAction = viewModel::onRetryClicked,
                    )
                }

                Status.Empty -> {
                    FeedEmpty(
                        modifier = Modifier.align(Alignment.Center),
                        onAction = viewModel::onFilterClicked,
                    )
                }

                Status.Loaded -> {
                    var isPageLoading by remember {
                        mutableStateOf(false)
                    }

                    LaunchedEffect(state.items) {
                        isPageLoading = false
                    }

                    FeedGrid(
                        list = state.items,
                        hasNextPage = hasNextPage,
                        isPageLoading = isPageLoading,
                        onLoadMore = {
                            viewModel.onLoadMore()
                            isPageLoading = true
                        },
                        onReleaseClicked = onReleaseClicked,
                    )
                }
            }
        }

        if (isFeedFilterShownState) {
            FeedFilterBottomSheet(
                catalogFilter = state.input.filterCatalogFilter,
                onClosed = viewModel::onFilterClosed,
                onFilterChanged = viewModel::onFilterChanged,
            )
        }
    }
}
