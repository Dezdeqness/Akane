package com.dezdeqness.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dezdeqness.designsystem.layouts.AkaneScene

@Composable
fun AppNavHost(
    navigator: AppNavigator,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
    activeDownloadsCount: Int,
    onTabSelected: (AppTab) -> Unit,
) {
    val entriesByTab = AppTab.entries.associateWith { tab ->
        key(tab) {
            rememberDecoratedNavEntries(
                backStack = navigator.tabs.getValue(tab),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = remember(entryProvider) { tabScoped(tab, entryProvider) },
            )
        }
    }

    val activeTab = navigator.activeTab
    val homeEntries = entriesByTab.getValue(AppTab.HOME)
    val entries = if (activeTab == AppTab.HOME) {
        homeEntries
    } else {
        homeEntries + entriesByTab.getValue(activeTab)
    }

    RootNavigationScaffold(
        activeTab = activeTab,
        navigationBars = AkaneScene.navigationBarsOf(entries.last().metadata),
        activeDownloadsCount = activeDownloadsCount,
        onTabSelected = onTabSelected,
    ) { modifier ->
        NavDisplay(
            entries = entries,
            modifier = modifier,
            onBack = { navigator.back() },
        )
    }
}

private fun tabScoped(
    tab: AppTab,
    provider: (NavKey) -> NavEntry<NavKey>,
): (NavKey) -> NavEntry<NavKey> = { key ->
    val entry = provider(key)
    NavEntry(
        key = key,
        contentKey = "${tab.id}/${entry.contentKey}",
        metadata = entry.metadata,
    ) {
        ShellEntryContent(navigationBars = AkaneScene.navigationBarsOf(entry.metadata)) {
            entry.Content()
        }
    }
}
