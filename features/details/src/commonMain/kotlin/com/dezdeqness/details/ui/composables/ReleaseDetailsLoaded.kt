package com.dezdeqness.details.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.designsystem.nestedscroll.CollapsingAppBarNestedScrollConnection
import com.dezdeqness.details.ui.ExpandedHeader
import com.dezdeqness.details.ui.composables.episodes.EpisodeItem
import com.dezdeqness.details.ui.composables.franchise.FranchiseTabContent
import com.dezdeqness.details.ui.composables.info.InfoTabContent
import com.dezdeqness.details.ui.composables.stats.StatisticsTabContent
import com.dezdeqness.details.ui.model.DetailsTab
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel

@Composable
fun ReleaseDetailsLoaded(
    modifier: Modifier = Modifier,
    details: ReleaseDetailsUiModel,
    onEpisodeClick: (Long, String) -> Unit,
    onBackPressed: () -> Unit,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val connection = remember { CollapsingAppBarNestedScrollConnection() }

    val tabs = details.tabs

    Surface(
        modifier = modifier.fillMaxSize(), color = AppTheme.colors.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection)
        ) {
            Column(
                modifier = Modifier.scrollable(
                    orientation = Orientation.Vertical,
                    state = rememberScrollableState { delta ->
                        0f
                    }
                ),
            ) {
                ExpandedHeader(
                    modifier = Modifier,
                    connection = connection,
                )

                ScrollableTabRow(
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
                            onClick = { selectedTabIndex = index },
                            text = { Text(item.title) }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(weight = 1f)
                        .background(color = AppTheme.colors.background)
                ) {
                    when (val item = tabs[selectedTabIndex]) {
                        is DetailsTab.InfoTab -> {
                            item {
                                InfoTabContent(info = item)
                            }
                        }

                        is DetailsTab.EpisodesTab -> {
                            items(item.episodes.size) { index ->
                                EpisodeItem(
                                    episode = item.episodes[index],
                                    onClick = {
                                        onEpisodeClick(details.id, item.episodes[index].id)
                                    }
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
