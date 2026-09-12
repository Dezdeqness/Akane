package com.dezdeqness.genre.ui.all

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AllGenresPage(
    onBackPressed: () -> Unit,
    onGenreClicked: (genreId: Int, genreName: String) -> Unit,
    viewModel: AllGenresViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    AllGenresContent(
        state = state,
        onBackPressed = onBackPressed,
        onRetryClicked = viewModel::onRetryClicked,
        onGenreClicked = onGenreClicked,
    )
}
