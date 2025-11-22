package com.dezdeqness.feed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.feed.ui.composable.FeedFilterBottomSheet
import com.dezdeqness.feed.ui.composable.FeedGrid
import com.dezdeqness.feed.ui.composable.FeedSearch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
    onReleaseClicked: (Long) -> Unit,
) {
    val state by viewModel.feedStateFlow.collectAsStateWithLifecycle()
    val isFeedFilterShownState by viewModel.isFeedFilterShownState.collectAsStateWithLifecycle()

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
        Box(modifier = Modifier.padding(contentPadding)) {
            if (state.status == Status.Initial || state.status == Status.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
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

        if (isFeedFilterShownState) {
            FeedFilterBottomSheet(
                catalogFilter = state.input.filterCatalogFilter,
                onClosed = viewModel::onFilterClosed,
                onFilterChanged = viewModel::onFilterChanged,
            )
        }
    }
}
