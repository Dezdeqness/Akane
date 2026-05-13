package com.dezdeqness.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.auth.contract.session.SessionState
import com.dezdeqness.auth.navigation.LoginRoute
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
import com.dezdeqness.profile.navigation.ProfileRoute
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
    val profileBackStack = rememberNavBackStack(navSavedStateConfiguration(), LoginRoute)

    var activeTabOrdinal by rememberSaveable { mutableStateOf(0) }
    val activeTab = AkaneBottomTabModel.entries[activeTabOrdinal].key

    val currentTabStack = when (activeTab) {
        HomeRoute -> homeBackStack
        FeedRoute -> feedBackStack
        PersonalRoute -> personalBackStack
        DownloadsRoute -> downloadsBackStack
        ProfileRoute -> profileBackStack
        else -> homeBackStack
    }

    val tabBackHandlerState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
    NavigationBackHandler(
        state = tabBackHandlerState,
        isBackEnabled = activeTabOrdinal != 0 && currentTabStack.size <= 1,
        onBackCompleted = { activeTabOrdinal = 0 },
    )

    val appViewModel: AppViewModel = koinViewModel()
    val activeDownloadsCount by appViewModel.activeDownloadsCount.collectAsState()
    val sessionState by appViewModel.sessionState.collectAsState()

    LaunchedEffect(sessionState) {
        when (sessionState) {
            SessionState.Authenticated -> {
                if (profileBackStack.firstOrNull() != ProfileRoute) {
                    profileBackStack.clear()
                    profileBackStack.add(ProfileRoute)
                }
            }
            SessionState.Unauthenticated -> {
                if (profileBackStack.firstOrNull() != LoginRoute) {
                    profileBackStack.clear()
                    profileBackStack.add(LoginRoute)
                }
            }
            SessionState.Loading -> Unit
        }
    }

    RootNavigationScaffold(
        activeTab = activeTab,
        activeDownloadsCount = activeDownloadsCount,
        onTabSelected = { tab ->
            val newOrdinal = AkaneBottomTabModel.entries.indexOfFirst { it.key == tab }
            if (newOrdinal >= 0) {
                if (activeTabOrdinal != newOrdinal) {
                    analytics.trackBottomNavigation(AkaneBottomTabModel.entries[newOrdinal].name)
                }
                activeTabOrdinal = newOrdinal
            }
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
            onNavigateToFeed = {
                activeTabOrdinal = AkaneBottomTabModel.entries.indexOfFirst { it.key == FeedRoute }
            },
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
