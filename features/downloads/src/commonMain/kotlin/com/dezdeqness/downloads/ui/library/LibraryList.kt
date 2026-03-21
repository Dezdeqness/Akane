package com.dezdeqness.downloads.ui.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.downloads.ui.model.ReleaseGroup

@Composable
internal fun LibraryList(
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
            ReleaseGroupItem(
                group = group,
                onClicked = { onReleaseClicked(group.releaseId) },
            )
        }
    }
}
