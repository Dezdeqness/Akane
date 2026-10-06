package com.dezdeqness.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.designsystem.layouts.AkaneScene

private val WideDeviceWidthFactor = 840.dp
private val SideNavigationWidth = 240.dp

@Composable
fun RootNavigationScaffold(
    activeTab: AppTab,
    navigationBars: AkaneScene.NavigationBars,
    activeDownloadsCount: Int,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val useRailNavigation = maxWidth >= WideDeviceWidthFactor
        val showBottomBar = !useRailNavigation && navigationBars == AkaneScene.NavigationBars.Visible
        val showRail = useRailNavigation && navigationBars != AkaneScene.NavigationBars.Hidden

        Scaffold(
            containerColor = AppTheme.colors.background,
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top),
                ) {
                    NavigationBar(
                        containerColor = AppTheme.colors.background,
                        tonalElevation = 0.dp,
                    ) {
                        RootNavigationItems(
                            activeTab = activeTab,
                        ) { item, isSelected ->
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { onTabSelected(item) },
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
            val layoutDirection = LocalLayoutDirection.current
            val horizontal = if (navigationBars == AkaneScene.NavigationBars.Hidden) {
                PaddingValues()
            } else {
                PaddingValues(
                    start = padding.calculateStartPadding(layoutDirection),
                    end = padding.calculateEndPadding(layoutDirection),
                )
            }
            val vertical = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal)
                    .consumeWindowInsets(horizontal),
            ) {
                AnimatedVisibility(visible = showRail) {
                    SideNavigation(
                        activeTab = activeTab,
                        activeDownloadsCount = activeDownloadsCount,
                        onTabSelected = onTabSelected,
                        modifier = Modifier.padding(vertical),
                    )
                }
                CompositionLocalProvider(
                    LocalShellInsets provides ShellInsets(padding = vertical, compact = !useRailNavigation),
                ) {
                    content(Modifier.weight(1f).fillMaxHeight())
                }
            }
        }
    }
}

@Immutable
private class ShellInsets(val padding: PaddingValues, val compact: Boolean)

private val LocalShellInsets = compositionLocalOf { ShellInsets(PaddingValues(), compact = true) }

@Composable
fun ShellEntryContent(
    navigationBars: AkaneScene.NavigationBars,
    content: @Composable () -> Unit,
) {
    val insets = LocalShellInsets.current
    val padded = when (navigationBars) {
        AkaneScene.NavigationBars.Visible -> true
        AkaneScene.NavigationBars.HideBottomBar -> !insets.compact
        AkaneScene.NavigationBars.Hidden -> false
    }
    val modifier = if (padded) {
        Modifier.padding(insets.padding).consumeWindowInsets(insets.padding)
    } else {
        Modifier
    }

    Box(modifier = modifier.fillMaxSize()) {
        content()
    }
}

@Composable
private fun SideNavigation(
    activeTab: AppTab,
    activeDownloadsCount: Int,
    onTabSelected: (AppTab) -> Unit,
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
            val showBadge = item == AppTab.DOWNLOADS &&
                    !isSelected &&
                    activeDownloadsCount > 0

            NavigationDrawerItem(
                selected = isSelected,
                onClick = { onTabSelected(item) },
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
    activeTab: AppTab,
    itemContent: @Composable (item: AppTab, isSelected: Boolean) -> Unit,
) {
    AppTab.entries.forEach { item ->
        val isSelected = activeTab == item
        itemContent(item, isSelected)
    }
}

@Composable
private fun AkaneNavigationItemIcon(
    item: AppTab,
    isSelected: Boolean,
    activeDownloadsCount: Int,
) {
    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
    val showBadge = item == AppTab.DOWNLOADS &&
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
