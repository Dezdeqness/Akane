package com.dezdeqness.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.details.navigation.detailsEntries
import com.dezdeqness.details.navigation.navigateToDetailsScreen
import com.dezdeqness.downloads.navigation.DownloadsRoute
import com.dezdeqness.downloads.navigation.activeDownloadsEntries
import com.dezdeqness.downloads.navigation.navigateToActiveDownloads
import com.dezdeqness.downloads.navigation.navigateToReleaseEpisodes
import com.dezdeqness.downloads.navigation.releaseEpisodesEntries
import com.dezdeqness.feed.navigation.FeedRoute
import com.dezdeqness.home.navigation.HomeRoute
import com.dezdeqness.personal.navigation.PersonalRoute
import com.dezdeqness.videoplayer.navigation.downloadedPlaylistEntries
import com.dezdeqness.videoplayer.navigation.navigateToDownloadedPlaylist
import com.dezdeqness.videoplayer.navigation.navigateToVideoPlayerScreen
import com.dezdeqness.videoplayer.navigation.videoPlayerEntries
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RootScreen(
    rootBackStack: NavBackStack<NavKey>,
) {
    val analytics: AkaneAnalytics = koinInject()

    val homeBackStack = rememberNavBackStack(navSavedStateConfiguration(), HomeRoute)
    val feedBackStack = rememberNavBackStack(navSavedStateConfiguration(), FeedRoute)
    val personalBackStack = rememberNavBackStack(navSavedStateConfiguration(), PersonalRoute)
    val downloadsBackStack = rememberNavBackStack(navSavedStateConfiguration(), DownloadsRoute)

    var activeTab: NavKey by rememberSaveable { mutableStateOf(HomeRoute) }

    val currentTabStack = when (activeTab) {
        HomeRoute -> homeBackStack
        FeedRoute -> feedBackStack
        PersonalRoute -> personalBackStack
        DownloadsRoute -> downloadsBackStack
        else -> homeBackStack
    }

    val appViewModel: AppViewModel = koinViewModel()
    val activeDownloadsCount by appViewModel.activeDownloadsCount.collectAsState()

    RootNavigationScaffold(
        activeTab = activeTab,
        activeDownloadsCount = activeDownloadsCount,
        onTabSelected = { tab ->
            if (activeTab != tab) {
                val tabName = AkaneBottomTabModel.entries.find { it.key == tab }?.name.orEmpty()
                analytics.trackBottomNavigation(tabName)
            }
            activeTab = tab
        },
    ) { contentModifier, isWideLayout ->
        RootNavigationHost(
            modifier = contentModifier,
            currentTabStack = currentTabStack,
            rootControllerNavigateToDetails = { id, title ->
                analytics.trackDetailsOpened(animeId = id, title = title)
                if (isWideLayout) {
                    currentTabStack.navigateToDetailsScreen(id)
                } else {
                    rootBackStack.navigateToDetailsScreen(id)
                }
            },
            activeDownloadsCountFlow = appViewModel.activeDownloadsCount,
            onNavigateToFeed = { activeTab = FeedRoute },
            onNavigateToReleaseEpisodes = { id ->
                if (isWideLayout) {
                    currentTabStack.navigateToReleaseEpisodes(id)
                } else {
                    rootBackStack.navigateToReleaseEpisodes(id)
                }
            },
            onNavigateToActiveDownloads = {
                if (isWideLayout) {
                    currentTabStack.navigateToActiveDownloads()
                } else {
                    rootBackStack.navigateToActiveDownloads()
                }
            },
            tabFullScreenEntries = {
                tabFullScreenEntriesForWideLayout(
                    currentTabStack = currentTabStack,
                    onEpisodeClick = { id, episodeId ->
                        currentTabStack.navigateToVideoPlayerScreen(id, episodeId)
                    },
                    onPlayDownloadedClicked = { releaseId, episodeId ->
                        currentTabStack.navigateToDownloadedPlaylist(releaseId, episodeId)
                    },
                )
            },
        )
    }
}

private fun EntryProviderScope<NavKey>.tabFullScreenEntriesForWideLayout(
    currentTabStack: NavBackStack<NavKey>,
    onEpisodeClick: (Long, String) -> Unit,
    onPlayDownloadedClicked: (Long, String) -> Unit,
) {
    detailsEntries(
        onBackPressed = { currentTabStack.removeLastOrNull() },
        onEpisodeClick = onEpisodeClick,
    )
    releaseEpisodesEntries(
        onBackPressed = { currentTabStack.removeLastOrNull() },
        onPlayClicked = onPlayDownloadedClicked,
    )
    activeDownloadsEntries(
        onBackPressed = { currentTabStack.removeLastOrNull() },
    )
    videoPlayerEntries(
        onBackPressed = { currentTabStack.removeLastOrNull() },
    )
    downloadedPlaylistEntries(
        onBackPressed = { currentTabStack.removeLastOrNull() },
    )
}
