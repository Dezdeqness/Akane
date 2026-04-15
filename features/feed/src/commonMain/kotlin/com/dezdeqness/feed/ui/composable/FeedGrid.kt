package com.dezdeqness.feed.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.dezdeqness.core.ui.views.rememberShimmerOffset
import com.dezdeqness.feed.ui.model.FeedAnimeUiModel

private const val PAGINATION_LOAD_FACTOR = 0.75
private const val CELL_GRID_COUNT = 3

@Composable
fun FeedGrid(
    modifier: Modifier = Modifier,
    list: List<FeedAnimeUiModel>,
    hasNextPage: Boolean,
    isPageLoading: Boolean,
    onLoadMore: () -> Unit,
    onReleaseClicked: (Long, String) -> Unit,
) {
    val gridState = rememberLazyGridState()

    val shimmerOffset by rememberShimmerOffset()

    val shouldStartPaginate = remember {
        derivedStateOf {
            hasNextPage && (gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -1) >= (gridState.layoutInfo.totalItemsCount * PAGINATION_LOAD_FACTOR)
        }
    }

    LaunchedEffect(isPageLoading, shouldStartPaginate.value) {
        if (shouldStartPaginate.value && isPageLoading.not()) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(CELL_GRID_COUNT),
        modifier = modifier.fillMaxSize(),
    ) {
        items(
            count = list.size,
            key = { index -> list[index].id }
        ) { index ->
            val item = list[index]

            val padding = calculateItemPadding(index, CELL_GRID_COUNT)

            FeedItem(
                item = item,
                modifier = Modifier
                    .animateItem()
                    .padding(padding),
                onReleaseClicked = {
                    val id = item.id
                    val title = item.title
                    onReleaseClicked.invoke(id, title)
                }
            )
        }

        if (hasNextPage) {
            repeat(CELL_GRID_COUNT) { index ->
                val padding = calculateItemPadding(index, CELL_GRID_COUNT)
                item {
                    FeedItemLoading(
                        modifier = Modifier.padding(padding), shimmerOffset = shimmerOffset,
                    )
                }
            }
        }
    }

}

fun calculateItemPadding(index: Int, cellCount: Int): PaddingValues {
    val column = index % cellCount
    val left = 8.dp - column * 8.dp / cellCount
    val right = (column + 1) * 8.dp / cellCount
    return PaddingValues(start = left, end = right, top = 8.dp)
}
