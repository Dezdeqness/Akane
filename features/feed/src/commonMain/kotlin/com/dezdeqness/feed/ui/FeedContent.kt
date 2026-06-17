package com.dezdeqness.feed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dezdeqness.catalog.ui.ReleaseGrid
import com.dezdeqness.catalog.ui.ReleaseListLoading
import com.dezdeqness.feed.ui.composable.FeedEmpty
import com.dezdeqness.feed.ui.composable.FeedError

@Composable
fun FeedContent(
    state: FeedState,
    onRetryClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onLoadMore: () -> Unit,
    onReleaseClicked: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    columns: Int = 3,
) {
    Box(
        modifier = modifier
            .padding(contentPadding)
            .fillMaxSize(),
    ) {
        when (state.status) {
            Status.Initial, Status.Loading -> {
                ReleaseListLoading(
                    modifier = Modifier.fillMaxSize(),
                    gridElementCount = columns,
                )
            }

            Status.Error -> {
                FeedError(
                    modifier = Modifier.align(Alignment.Center),
                    onAction = onRetryClicked,
                )
            }

            Status.Empty -> {
                FeedEmpty(
                    modifier = Modifier.align(Alignment.Center),
                    onAction = onFilterClicked,
                )
            }

            Status.Loaded -> {
                var isPageLoading by remember {
                    mutableStateOf(false)
                }

                LaunchedEffect(state.items) {
                    isPageLoading = false
                }

                ReleaseGrid(
                    list = state.items,
                    hasNextPage = state.hasNextPage,
                    isPageLoading = isPageLoading,
                    columnCount = columns,
                    onLoadMore = {
                        onLoadMore()
                        isPageLoading = true
                    },
                    onReleaseClicked = onReleaseClicked,
                )
            }
        }
    }
}
