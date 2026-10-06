package com.dezdeqness.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.auth.contract.session.SessionState
import com.dezdeqness.auth.navigation.LoginRoute
import com.dezdeqness.core.ui.views.image.LocalAstImageLoader
import com.dezdeqness.designsystem.AkaneTheme
import com.dezdeqness.designsystem.AkaneThemeSpec
import com.dezdeqness.designsystem.DarkMobileTheme
import com.dezdeqness.designsystem.LightTheme
import com.dezdeqness.designsystem.LocalSystemBarIconsStack
import com.dezdeqness.designsystem.SystemBarIcons
import com.dezdeqness.designsystem.SystemBarIconsStack
import com.dezdeqness.designsystem.imageloader.getImageLoader
import com.dezdeqness.details.navigation.navigateToDetailsScreen
import com.dezdeqness.profile.navigation.ProfileRoute
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    theme: AkaneThemeSpec = if (isSystemInDarkTheme()) DarkMobileTheme else LightTheme,
) {
    CompositionLocalProvider(
        LocalAstImageLoader provides getImageLoader(),
        LocalSystemBarIconsStack provides remember { SystemBarIconsStack() },
    ) {
        AkaneTheme(theme = theme) {
            SystemBarIcons(lightIcons = theme.isDark)

            val analytics: AkaneAnalytics = koinInject()
            val appViewModel: AppViewModel = koinViewModel()
            val navigator = rememberAppNavigator()

            val activeDownloadsCount by appViewModel.activeDownloadsCount.collectAsState()
            val sessionState by appViewModel.sessionState.collectAsState()

            LaunchedEffect(sessionState) {
                when (sessionState) {
                    SessionState.Authenticated -> navigator.resetTab(AppTab.PROFILE, ProfileRoute)
                    SessionState.Unauthenticated -> navigator.resetTab(AppTab.PROFILE, LoginRoute)
                    SessionState.Loading -> Unit
                }
            }

            val entryProvider = remember(navigator) {
                appEntryProvider(
                    navigator = navigator,
                    activeDownloadsCount = appViewModel.activeDownloadsCount,
                    stack = { navigator.currentStack },
                    back = { navigator.back() },
                    openDetails = { id, title ->
                        analytics.trackDetailsOpened(animeId = id, title = title)
                        navigator.currentStack.navigateToDetailsScreen(id)
                    },
                )
            }

            AppNavHost(
                navigator = navigator,
                entryProvider = entryProvider,
                activeDownloadsCount = activeDownloadsCount,
                onTabSelected = { tab ->
                    if (tab != navigator.activeTab) {
                        analytics.trackBottomNavigation(tab.name)
                    }
                    navigator.switchTab(tab)
                },
            )
        }
    }
}
