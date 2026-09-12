package com.dezdeqness.downloads.ui.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryPage(
    onReleaseClicked: (releaseId: Long) -> Unit,
    activeDownloadsCountFlow: Flow<Int> = emptyFlow(),
    onActiveDownloadsClicked: () -> Unit = {},
    viewModel: LibraryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()
    val activeDownloadsCount by activeDownloadsCountFlow.collectAsState(initial = 0)

    LibraryContent(
        state = state,
        activeDownloadsCount = activeDownloadsCount,
        onReleaseClicked = onReleaseClicked,
        onActiveDownloadsClicked = onActiveDownloadsClicked,
    )
}
