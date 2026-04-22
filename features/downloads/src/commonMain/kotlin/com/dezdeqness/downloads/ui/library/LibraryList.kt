package com.dezdeqness.downloads.ui.library

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.downloads.ui.model.ReleaseGroup

private val TABLET_TARGET_WIDTH = 200.dp
private val TABLET_DESKTOP_WIDTH = 350.dp

@Composable
fun LibraryListWide(
    library: List<ReleaseGroup>,
    onReleaseClicked: (releaseId: Long) -> Unit,
    layoutType: LayoutType,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(
            if (layoutType == LayoutType.Tablet) {
                TABLET_TARGET_WIDTH
            } else {
                TABLET_DESKTOP_WIDTH
            }
        ),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(
            count = library.size,
            key = { index -> library[index].releaseId },
        ) { index ->
            val item = library[index]
            ReleaseGroupItemWide(
                group = item,
                onClicked = { onReleaseClicked(item.releaseId) },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun LibraryListMobile(
    library: List<ReleaseGroup>,
    onReleaseClicked: (releaseId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(
            items = library,
            key = { it.releaseId },
        ) { group ->
            ReleaseGroupItemMobile(
                group = group,
                onClicked = { onReleaseClicked(group.releaseId) },
            )
        }
    }
}
