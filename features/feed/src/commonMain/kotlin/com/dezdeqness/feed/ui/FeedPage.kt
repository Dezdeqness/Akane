package com.dezdeqness.feed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.feed.ui.composable.FeedItem
import org.koin.compose.viewmodel.koinViewModel

private const val PAGINATION_LOAD_FACTOR = 0.75
private const val CELL_GRID_COUNT = 3

@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
    onReleaseClicked: (Long) -> Unit,
) {
    var isPageLoading by remember {
        mutableStateOf(false)
    }

    val state by viewModel.feedStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(state.items) {
        isPageLoading = false
    }

    val hasNextPage = state.hasNextPage

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppTheme.colors.background,
    ) {
        if (state.status == Status.Initial || state.status == Status.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            val gridState = rememberLazyGridState()

            val shouldStartPaginate = remember {
                derivedStateOf {
                    hasNextPage && (gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: -1) >= (gridState.layoutInfo.totalItemsCount * PAGINATION_LOAD_FACTOR)
                }
            }

            LaunchedEffect(isPageLoading, shouldStartPaginate.value) {
                if (shouldStartPaginate.value && isPageLoading.not()) {
                    viewModel.onLoadMore()
                    isPageLoading = true
                }
            }

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(CELL_GRID_COUNT),
                modifier = modifier.fillMaxSize(),
            ) {
                items(
                    count = state.items.size,
                    key = { index -> state.items[index].id }
                ) { index ->
                    val item = state.items[index]

                    val padding = calculateItemPadding(index, CELL_GRID_COUNT)

                    FeedItem(
                        item = item,
                        modifier = Modifier
                            .animateItem()
                            .padding(padding),
                        onReleaseClicked = { id ->
                            onReleaseClicked.invoke(item.id)
                        }
                    )
                }

                if (hasNextPage) {
                    repeat(CELL_GRID_COUNT) { index ->
                        val padding = calculateItemPadding(index, CELL_GRID_COUNT)
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(padding),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calculateItemPadding(index: Int, cellCount: Int): PaddingValues {
    val column = index % cellCount
    val left = 8.dp - column * 8.dp / cellCount
    val right = (column + 1) * 8.dp / cellCount
    return PaddingValues(start = left, end = right, top = 8.dp)
}
