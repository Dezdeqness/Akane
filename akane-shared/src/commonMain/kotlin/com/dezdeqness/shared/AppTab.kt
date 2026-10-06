package com.dezdeqness.shared

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.auth.navigation.LoginRoute
import com.dezdeqness.downloads.navigation.DownloadsRoute
import com.dezdeqness.feed.navigation.FeedRoute
import com.dezdeqness.home.navigation.HomeRoute
import com.dezdeqness.personal.navigation.PersonalRoute

enum class AppTab(
    val id: String,
    val startRoute: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        id = "home",
        startRoute = HomeRoute,
        label = "Главная",
        selectedIcon = AkaneIcons.Home,
        unselectedIcon = AkaneIcons.HomeBorder,
    ),
    PERSONAL(
        id = "personal",
        startRoute = PersonalRoute,
        label = "Сохранённое",
        selectedIcon = AkaneIcons.Personal,
        unselectedIcon = AkaneIcons.PersonalBorder,
    ),
    SEARCH(
        id = "search",
        startRoute = FeedRoute,
        label = "Поиск",
        selectedIcon = AkaneIcons.Search,
        unselectedIcon = AkaneIcons.SearchBorder,
    ),
    DOWNLOADS(
        id = "downloads",
        startRoute = DownloadsRoute,
        label = "Загрузки",
        selectedIcon = AkaneIcons.Library,
        unselectedIcon = AkaneIcons.LibraryBorder,
    ),
    PROFILE(
        id = "profile",
        startRoute = LoginRoute,
        label = "Профиль",
        selectedIcon = AkaneIcons.Profile,
        unselectedIcon = AkaneIcons.ProfileBoarder,
    );

    companion object {
        fun fromId(id: String): AppTab = entries.firstOrNull { it.id == id } ?: HOME
    }
}
