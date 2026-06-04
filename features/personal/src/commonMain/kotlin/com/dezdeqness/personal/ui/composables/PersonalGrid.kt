package com.dezdeqness.personal.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dezdeqness.personal.ui.model.PersonalUiModel

private const val PAGINATION_LOAD_FACTOR = 0.75

@Composable
fun PersonalGrid(
    modifier: Modifier = Modifier,
    columns: GridCells = GridCells.Fixed(2),
    items: List<PersonalUiModel>,
    hasNextPage: Boolean,
    isPageLoading: Boolean,
    onLoadMore: () -> Unit,
    onItemClicked: (PersonalUiModel) -> Unit,
) {
    val gridState = rememberLazyGridState()

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
        columns = columns,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            PersonalCell(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onItemClicked(item) },
                item = item,
            )
        }
    }
}
