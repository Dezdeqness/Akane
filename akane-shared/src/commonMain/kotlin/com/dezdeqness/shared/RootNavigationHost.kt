package com.dezdeqness.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dezdeqness.auth.navigation.authEntries
import com.dezdeqness.downloads.navigation.downloadsEntries
import com.dezdeqness.feed.navigation.feedEntries
import com.dezdeqness.home.navigation.homeEntries
import com.dezdeqness.personal.navigation.personalEntries
import com.dezdeqness.profile.navigation.profileEntries
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RootNavigationHost(
    modifier: Modifier = Modifier,
    currentTabStack: NavBackStack<NavKey>,
    rootControllerNavigateToDetails: (Long, String) -> Unit,
    activeDownloadsCountFlow: StateFlow<Int>,
    onNavigateToFeed: () -> Unit,
    onNavigateToReleaseEpisodes: (Long) -> Unit,
    onNavigateToActiveDownloads: () -> Unit,
    tabFullScreenEntries: EntryProviderScope<NavKey>.() -> Unit = {},
) {
    NavDisplay(
        backStack = currentTabStack,
        onBack = { currentTabStack.removeLastOrNull() },
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            homeEntries(onItemClicked = rootControllerNavigateToDetails)
            feedEntries(onReleaseClicked = rootControllerNavigateToDetails)
            personalEntries(
                onItemClicked = rootControllerNavigateToDetails,
                onEmptyListActionClicked = onNavigateToFeed,
            )
            downloadsEntries(
                onReleaseClicked = onNavigateToReleaseEpisodes,
                activeDownloadsCountFlow = activeDownloadsCountFlow,
                onActiveDownloadsClicked = onNavigateToActiveDownloads,
            )
            profileEntries()
            authEntries(currentTabStack)
            tabFullScreenEntries()
        }
    )
}
