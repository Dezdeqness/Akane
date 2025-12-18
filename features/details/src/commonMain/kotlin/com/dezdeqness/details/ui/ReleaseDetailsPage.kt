package com.dezdeqness.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.details.ui.composables.ReleaseDetailsLoaded
import com.dezdeqness.details.ui.composables.ReleaseError
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPage(
    modifier: Modifier = Modifier,
    viewModel: ReleaseDetailsViewModel = koinViewModel(),
    onEpisodeClick: (Long, String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.releaseDetailsStateFlow.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        when (state.status) {
            Status.Error -> {
                ReleaseError(
                    modifier = Modifier.align(Alignment.Center),
                    onAction = viewModel::onRetryClicked,
                )
            }

            Status.Loading,
            Status.Initial,
                -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            Status.Loaded -> {
                val details = state.details ?: return@Box
                ReleaseDetailsLoaded(
                    details = details,
                    onEpisodeClick = onEpisodeClick,
                    onBackPressed = onBackPressed,
                    isFavourite = state.isFavourite,
                ) {
                    viewModel.onFavouriteClicked(details.id)
                }
            }
        }

    }
}
