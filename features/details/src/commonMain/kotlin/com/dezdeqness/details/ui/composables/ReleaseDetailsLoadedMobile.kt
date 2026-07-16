package com.dezdeqness.details.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppErrorButton
import com.dezdeqness.core.ui.views.buttons.AppPrimaryButton
import com.dezdeqness.designsystem.nestedscroll.CollapsingAppBarNestedScrollConnection
import com.dezdeqness.designsystem.nestedscroll.ExpandedHeader
import com.dezdeqness.designsystem.nestedscroll.LocalNestedScrollConnection
import com.dezdeqness.details.ui.composables.episodes.EpisodeItem
import com.dezdeqness.details.ui.composables.franchise.FranchiseTabContent
import com.dezdeqness.details.ui.composables.info.InfoTabContent
import com.dezdeqness.details.ui.composables.stats.StatisticsTabContent
import com.dezdeqness.details.ui.model.DetailsTab
import com.dezdeqness.details.ui.model.DownloadStatusUi
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.details.ui.model.FavouriteButtonState
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel

@Composable
fun ReleaseDetailsLoadedMobile(
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
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val connection = remember { CollapsingAppBarNestedScrollConnection(scope) }

    val tabs = details.tabs

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex != 0) {
            connection.collapse()
            connection.isScrollEnabled = false
        } else {
            connection.isScrollEnabled = true
        }
    }

    CompositionLocalProvider(
        LocalNestedScrollConnection provides connection,
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = AppTheme.colors.background,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(connection),
            ) {
                Column(
                    modifier = Modifier.scrollable(
                        orientation = Orientation.Vertical,
                        state = rememberScrollableState { 0f },
                    ),
                ) {
                    ExpandedHeader(
                        header = {
                            ReleaseHeader(
                                modifier = Modifier.graphicsLayer {
                                    translationY = -connection.headerOffset * 0.5f
                                },
                                header = details.header,
                            )
                        },
                        navigationBar = {
                            ReleaseToolbar(
                                title = details.header.title,
                                favouriteButtonState = favouriteButtonState,
                                onBackPressed = onBackPressed,
                                onFavouriteClicked = onFavouriteClicked,
                            )
                        },
                    )

                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = AppTheme.colors.background,
                        contentColor = AppTheme.colors.textPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = 16.dp,
                        divider = {},
                    ) {
                        tabs.forEachIndexed { index, item ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    selectedTabIndex = index
                                },
                                text = { Text(item.title) },
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(weight = 1f)
                            .background(color = AppTheme.colors.background),
                    ) {
                        when (val item = tabs[selectedTabIndex]) {
                            is DetailsTab.InfoTab -> {
                                item {
                                    InfoTabContent(info = item)
                                }
                            }

                            is DetailsTab.EpisodesTab -> {
                                val activeStatuses = setOf(
                                    DownloadStatusUi.QUEUED,
                                    DownloadStatusUi.DOWNLOADING,
                                    DownloadStatusUi.PAUSED,
                                )
                                val hasActiveDownloads = item.episodes.any {
                                    it.downloadStatus in activeStatuses
                                }

                                if (item.episodes.size > 1) {
                                    item {
                                        if (hasActiveDownloads) {
                                            AppErrorButton(
                                                title = "Отменить все",
                                                onClick = onCancelAllDownloadsClick,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Default.Cancel,
                                                        contentDescription = null,
                                                    )
                                                },
                                            )
                                        } else {
                                            AppPrimaryButton(
                                                title = "Скачать все",
                                                onClick = onDownloadAllClick,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Default.Download,
                                                        contentDescription = null,
                                                    )
                                                },
                                            )
                                        }
                                    }
                                }
                                items(item.episodes.size) { index ->
                                    EpisodeItem(
                                        episode = item.episodes[index],
                                        onClick = {
                                            onEpisodeClick(details.id, item.episodes[index].id)
                                        },
                                        onDownloadClick = {
                                            onDownloadClick(item.episodes[index])
                                        },
                                        onCancelDownloadClick = {
                                            onCancelDownloadClick(item.episodes[index].id)
                                        },
                                    )
                                }
                            }

                            is DetailsTab.FranchiseTab -> {
                                item {
                                    FranchiseTabContent(franchise = item)
                                }
                            }

                            is DetailsTab.StatisticsTab -> {
                                item {
                                    StatisticsTabContent(statistics = item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavBar() {
    val connection = LocalNestedScrollConnection.current

    var alphaValue by remember { mutableFloatStateOf(0f) }

    alphaValue = (3 * (1f - connection.progress)).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = alphaValue),
            )
            .background(Color.Transparent),
    ) {
    }
}
