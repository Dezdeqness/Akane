package com.dezdeqness.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.core.ui.theme.AppTheme

private val WideDeviceWidthFactor = 840.dp
private val SideNavigationWidth = 240.dp

@Composable
fun RootNavigationScaffold(
    activeTab: NavKey?,
    activeDownloadsCount: Int,
    onTabSelected: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (modifier: Modifier, isWideLayout: Boolean) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val useRailNavigation = maxWidth >= WideDeviceWidthFactor

        Scaffold(
            bottomBar = {
                if (useRailNavigation.not()) {
                    NavigationBar(
                        containerColor = AppTheme.colors.background,
                        tonalElevation = 0.dp,
                    ) {
                        RootNavigationItems(
                            activeTab = activeTab,
                        ) { item, isSelected ->
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { onTabSelected(item.key) },
                                icon = {
                                    AkaneNavigationItemIcon(
                                        item = item,
                                        isSelected = isSelected,
                                        activeDownloadsCount = activeDownloadsCount,
                                    )
                                },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            if (useRailNavigation) {
                Row(modifier = Modifier.fillMaxSize()) {
                    SideNavigation(
                        activeTab = activeTab,
                        activeDownloadsCount = activeDownloadsCount,
                        onTabSelected = onTabSelected,
                    )
                    content(Modifier.fillMaxSize().weight(1f), true)
                }
            } else {
                content(Modifier.fillMaxSize().padding(padding), false)
            }
        }
    }
}


@Composable
private fun SideNavigation(
    activeTab: NavKey?,
    activeDownloadsCount: Int,
    onTabSelected: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(SideNavigationWidth)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        RootNavigationItems(
            activeTab = activeTab,
        ) { item, isSelected ->
            val showBadge = item == AkaneBottomTabModel.DOWNLOADS &&
                    !isSelected &&
                    activeDownloadsCount > 0

            NavigationDrawerItem(
                selected = isSelected,
                onClick = { onTabSelected(item.key) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label) },
                badge = if (showBadge) {
                    { Badge { Text(activeDownloadsCount.toString()) } }
                } else {
                    null
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = AppTheme.colors.background,
                ),
            )
        }
    }
}

@Composable
private fun RootNavigationItems(
    activeTab: NavKey?,
    itemContent: @Composable (item: AkaneBottomTabModel, isSelected: Boolean) -> Unit,
) {
    AkaneBottomTabModel.entries.forEach { item ->
        val isSelected = activeTab == item.key
        itemContent(item, isSelected)
    }
}

@Composable
private fun AkaneNavigationItemIcon(
    item: AkaneBottomTabModel,
    isSelected: Boolean,
    activeDownloadsCount: Int,
) {
    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
    val showBadge = item == AkaneBottomTabModel.DOWNLOADS &&
            !isSelected &&
            activeDownloadsCount > 0

    if (showBadge) {
        BadgedBox(
            badge = {
                Badge { Text(activeDownloadsCount.toString()) }
            },
        ) {
            Icon(
                imageVector = icon,
                contentDescription = item.label,
            )
        }
    } else {
        Icon(
            imageVector = icon,
            contentDescription = item.label,
        )
    }
}
