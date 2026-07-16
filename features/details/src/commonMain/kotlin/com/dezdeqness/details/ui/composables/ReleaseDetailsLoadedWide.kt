package com.dezdeqness.details.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.details.ui.composables.adaptive.ReleaseDetailsWideContent
import com.dezdeqness.details.ui.composables.adaptive.ReleaseDetailsWideSidebar
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.details.ui.model.FavouriteButtonState
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel

private val DetailsWideSidebarWidth = 420.dp

@Composable
fun ReleaseDetailsLoadedWide(
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
    var selectedTabIndex by remember { mutableStateOf(0) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppTheme.colors.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            AppToolbar(
                title = {
                    Text(
                        text = details.header.title,
                        color = AppTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigation = {
                    AppIconButton(
                        onClick = onBackPressed,
                        contentColor = AppTheme.colors.surface,
                    ) {
                        Icon(
                            AkaneIcons.Back,
                            contentDescription = null,
                            tint = AppTheme.colors.textPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults
                    .topAppBarColors()
                    .copy(containerColor = AppTheme.colors.background),
            )


            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    ReleaseDetailsWideSidebar(
                        modifier = Modifier.width(DetailsWideSidebarWidth),
                        details = details,
                        favouriteButtonState = favouriteButtonState,
                        onFavouriteClicked = onFavouriteClicked,
                    )

                    ReleaseDetailsWideContent(
                        modifier = Modifier.weight(1f),
                        details = details,
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it },
                        onEpisodeClick = onEpisodeClick,
                        onDownloadClick = onDownloadClick,
                        onCancelDownloadClick = onCancelDownloadClick,
                        onDownloadAllClick = onDownloadAllClick,
                        onCancelAllDownloadsClick = onCancelAllDownloadsClick,
                        onReleaseClicked = onReleaseClicked,
                    )
                }

            }
        }
    }
}
