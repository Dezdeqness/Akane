package com.dezdeqness.details.ui.composables

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.details.ui.model.FavouriteButtonState
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel

@Composable
fun ReleaseDetailsLoaded(
    modifier: Modifier = Modifier,
    details: ReleaseDetailsUiModel,
    onEpisodeClick: (Long, String) -> Unit,
    onDownloadClick: (EpisodesUiModel) -> Unit,
    onCancelDownloadClick: (String) -> Unit,
    onDownloadAllClick: () -> Unit,
    onCancelAllDownloadsClick: () -> Unit,
    onBackPressed: () -> Unit,
    favouriteButtonState: FavouriteButtonState,
    onFavouriteClicked: () -> Unit,
    onReleaseClicked: (Long, String) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        AdaptiveLayout(modifier = modifier.fillMaxSize()) {
            val type = LocalLayoutType.current

            when (type) {
                LayoutType.Mobile -> {
                    ReleaseDetailsLoadedMobile(
                        details = details,
                        onEpisodeClick = onEpisodeClick,
                        onDownloadClick = onDownloadClick,
                        onCancelDownloadClick = onCancelDownloadClick,
                        onDownloadAllClick = onDownloadAllClick,
                        onCancelAllDownloadsClick = onCancelAllDownloadsClick,
                        onBackPressed = onBackPressed,
                        favouriteButtonState = favouriteButtonState,
                        onFavouriteClicked = onFavouriteClicked,
                        onReleaseClicked = onReleaseClicked,
                    )
                }

                LayoutType.Desktop,
                LayoutType.Tablet -> {
                    ReleaseDetailsLoadedWide(
                        details = details,
                        onEpisodeClick = onEpisodeClick,
                        onDownloadClick = onDownloadClick,
                        onCancelDownloadClick = onCancelDownloadClick,
                        onDownloadAllClick = onDownloadAllClick,
                        onCancelAllDownloadsClick = onCancelAllDownloadsClick,
                        onBackPressed = onBackPressed,
                        favouriteButtonState = favouriteButtonState,
                        onFavouriteClicked = onFavouriteClicked,
                        onReleaseClicked = onReleaseClicked,
                    )
                }
            }
        }
    }
}
