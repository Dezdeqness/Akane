package com.dezdeqness.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

@Stable
class AppNavigator(
    val tabs: Map<AppTab, NavBackStack<NavKey>>,
    private val activeTabId: MutableState<String>,
) {
    val activeTab: AppTab
        get() = AppTab.fromId(activeTabId.value)

    val currentStack: NavBackStack<NavKey>
        get() = tabs.getValue(activeTab)

    fun back(): Boolean {
        val stack = currentStack
        return when {
            stack.size > 1 -> {
                stack.removeLastOrNull()
                true
            }

            activeTab != AppTab.HOME -> {
                switchTab(AppTab.HOME)
                true
            }

            else -> false
        }
    }

    fun switchTab(tab: AppTab) {
        activeTabId.value = tab.id
    }

    fun resetTab(tab: AppTab, rootKey: NavKey) {
        val stack = tabs.getValue(tab)
        if (stack.firstOrNull() != rootKey) {
            stack.clear()
            stack.add(rootKey)
        }
    }
}

@Composable
internal fun rememberAppNavigator(): AppNavigator {
    val tabs = AppTab.entries.associateWith { tab ->
        key(tab) { rememberNavBackStack(navSavedStateConfiguration(), tab.startRoute) }
    }
    val activeTabId = rememberSaveable { mutableStateOf(AppTab.HOME.id) }

    return remember { AppNavigator(tabs, activeTabId) }
}
