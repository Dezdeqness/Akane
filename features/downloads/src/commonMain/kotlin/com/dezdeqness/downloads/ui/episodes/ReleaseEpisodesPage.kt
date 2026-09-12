package com.dezdeqness.downloads.ui.episodes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReleaseEpisodesPage(
    onPlayClicked: (releaseId: Long, episodeId: String) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: ReleaseEpisodesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    ReleaseEpisodesContent(
        state = state,
        onBackPressed = onBackPressed,
        onPlayClicked = onPlayClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
    )
}
