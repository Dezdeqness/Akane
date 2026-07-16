package com.dezdeqness.details.ui.composables.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppErrorButton
import com.dezdeqness.core.ui.views.buttons.AppPrimaryButton
import com.dezdeqness.details.ui.composables.episodes.EpisodeItem
import com.dezdeqness.details.ui.composables.franchise.FranchiseTabContent
import com.dezdeqness.details.ui.composables.info.InfoTabContent
import com.dezdeqness.details.ui.composables.stats.StatisticsTabContent
import com.dezdeqness.details.ui.model.DetailsTab
import com.dezdeqness.details.ui.model.DownloadStatusUi
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel
import kotlin.collections.contains

@Composable
fun ReleaseDetailsWideContent(
    modifier: Modifier = Modifier,
    details: ReleaseDetailsUiModel,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onEpisodeClick: (Long, String) -> Unit,
    onDownloadClick: (EpisodesUiModel) -> Unit,
    onCancelDownloadClick: (String) -> Unit,
    onDownloadAllClick: () -> Unit,
    onCancelAllDownloadsClick: () -> Unit,
    onReleaseClicked: (Long, String) -> Unit,
) {
    val tabs = details.tabs

    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
        ) {
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
                        onClick = { onTabSelected(index) },
                        text = { Text(item.title) },
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.widthIn(max = 900.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Эпизоды · ${item.episodes.size}",
                                        style = AppTheme.typography.titleMedium,
                                        color = AppTheme.colors.textPrimary,
                                    )

                                    if (hasActiveDownloads) {
                                        AppErrorButton(
                                            title = "Отменить все",
                                            onClick = onCancelAllDownloadsClick,
                                            leadingIcon = {
                                                Icon(Icons.Default.Cancel, null)
                                            },
                                        )
                                    } else {
                                        AppPrimaryButton(
                                            title = "Скачать все",
                                            onClick = onDownloadAllClick,
                                            leadingIcon = {
                                                Icon(Icons.Default.Download, null)
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        items(item.episodes) { episode ->
                            EpisodeItem(
                                episode = episode,
                                onClick = {
                                    onEpisodeClick(details.id, episode.id)
                                },
                                onDownloadClick = {
                                    onDownloadClick(episode)
                                },
                                onCancelDownloadClick = {
                                    onCancelDownloadClick(episode.id)
                                },
                            )
                        }
                    }

                    is DetailsTab.FranchiseTab -> {
                        item {
                            FranchiseTabContent(
                                franchise = item,
                                onReleaseClicked = onReleaseClicked,
                            )
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
