package com.dezdeqness.genre.ui.releases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GenreReleasesPage(
    genreName: String,
    onBackPressed: () -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
    viewModel: GenreReleasesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    GenreReleasesContent(
        state = state,
        genreName = genreName,
        onBackPressed = onBackPressed,
        onRetryClicked = viewModel::onRetryClicked,
        onLoadMore = viewModel::onLoadMore,
        onReleaseClicked = onReleaseClicked,
    )
}
