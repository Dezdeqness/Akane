package com.dezdeqness.shared

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme

private val WideDeviceWidthFactor = 840.dp

@Composable
fun RootNavigationScaffold(
    currentDestination: String?,
    activeDownloadsCount: Int,
    onTabSelected: (String) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
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
                            currentDestination = currentDestination,
                        ) { item, isSelected ->
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { onTabSelected(item.route) },
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
                    NavigationRail(
                        containerColor = AppTheme.colors.background,
                    ) {
                        RootNavigationItems(
                            currentDestination = currentDestination,
                        ) { item, isSelected ->
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { onTabSelected(item.route) },
                                icon = {
                                    AkaneNavigationItemIcon(
                                        item = item,
                                        isSelected = isSelected,
                                        activeDownloadsCount = activeDownloadsCount,
                                    )
                                },
                                label = {
                                    Text(item.label)
                                },
                            )
                        }
                    }
                    content(Modifier.fillMaxSize().weight(1f))
                }
            } else {
                content(Modifier.fillMaxSize().padding(padding))
            }
        }
    }
}


@Composable
private fun RootNavigationItems(
    currentDestination: String?,
    itemContent: @Composable (item: AkaneBottomTabModel, isSelected: Boolean) -> Unit,
) {
    AkaneBottomTabModel.entries.forEach { item ->
        val isSelected = currentDestination == item.route
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
