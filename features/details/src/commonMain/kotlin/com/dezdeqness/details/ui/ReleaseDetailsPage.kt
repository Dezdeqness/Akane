package com.dezdeqness.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.details.ui.composables.ReleaseDetailsLoaded
import com.dezdeqness.details.ui.composables.ReleaseError
import com.dezdeqness.details.ui.composables.ReleaseLoading
import com.dezdeqness.details.ui.composables.ReleaseToolbarInitial
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPage(
    modifier: Modifier = Modifier,
    viewModel: ReleaseDetailsViewModel = koinViewModel(),
    onEpisodeClick: (Long, String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.releaseDetailsStateFlow.collectAsStateOnLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        when (state.status) {
            Status.Error -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    ReleaseToolbarInitial(onBackPressed = onBackPressed)
                    ReleaseError(
                        modifier = Modifier.fillMaxSize(),
                        onAction = viewModel::onRetryClicked,
                    )
                }
            }

            Status.Loading,
            Status.Initial,
                -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    ReleaseToolbarInitial(onBackPressed = onBackPressed)
                    ReleaseLoading(
                        modifier = Modifier.fillMaxSize(),
                    )
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
