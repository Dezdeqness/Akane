package com.dezdeqness.designsystem.layouts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val TABLET_TARGET_WIDTH = 200.dp
private val TABLET_DESKTOP_WIDTH = 350.dp

@Composable
fun <T> AdaptiveViewCollection(
    items: List<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mobileItem: @Composable (T) -> Unit,
    wideItem: @Composable (T) -> Unit,
) {
    AdaptiveLayout(modifier = modifier.fillMaxSize()) {
        when (val type = LocalLayoutType.current) {
            LayoutType.Mobile -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                ) {
                    items(
                        items = items,
                        key = key,
                    ) { item ->
                        mobileItem(item)
                    }
                }
            }

            else -> {
                val minSize = if (type == LayoutType.Tablet) {
                    TABLET_TARGET_WIDTH
                } else {
                    TABLET_DESKTOP_WIDTH
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                ) {
                    items(
                        count = items.size,
                        key = { index -> key(items[index]) },
                    ) { index ->
                        wideItem(items[index])
                    }
                }
            }
        }
    }
}