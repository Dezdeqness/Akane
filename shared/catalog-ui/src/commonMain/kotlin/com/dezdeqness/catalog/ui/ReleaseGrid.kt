package com.dezdeqness.catalog.ui

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
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel
import com.dezdeqness.core.ui.views.rememberShimmerOffset

private const val PAGINATION_LOAD_FACTOR = 0.75

@Composable
fun ReleaseGrid(
    modifier: Modifier = Modifier,
    list: List<ReleaseListUiModel>,
    hasNextPage: Boolean,
    isPageLoading: Boolean,
    columnCount: Int = 3,
    contentPadding: PaddingValues = PaddingValues(),
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
        columns = GridCells.Fixed(columnCount),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        items(
            count = list.size,
            key = { index -> list[index].id }
        ) { index ->
            val item = list[index]

            val padding = calculateItemPadding(index, columnCount)

            ReleaseCard(
                item = item,
                modifier = Modifier
                    .animateItem()
                    .padding(padding),
                onReleaseClicked = {
                    onReleaseClicked.invoke(item.id, item.title)
                }
            )
        }

        if (hasNextPage) {
            repeat(columnCount) { index ->
                val padding = calculateItemPadding(index, columnCount)
                item {
                    ReleaseItemLoading(
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
