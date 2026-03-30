package com.dezdeqness.shared

import androidx.compose.ui.graphics.vector.ImageVector
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.downloads.navigation.DOWNLOADS_ROUTE
import com.dezdeqness.feed.navigation.FEED_ROUTE
import com.dezdeqness.home.navigation.HOME_ROUTE
import com.dezdeqness.personal.navigation.PERSONAL_ROUTE

internal enum class AkaneBottomTabModel(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        route = HOME_ROUTE,
        label = "Главная",
        selectedIcon = AkaneIcons.Home,
        unselectedIcon = AkaneIcons.HomeBorder,
    ),
    PERSONAL(
        route = PERSONAL_ROUTE,
        label = "Сохранённое",
        selectedIcon = AkaneIcons.Personal,
        unselectedIcon = AkaneIcons.PersonalBorder,
    ),
    SEARCH(
        route = FEED_ROUTE,
        label = "Поиск",
        selectedIcon = AkaneIcons.Search,
        unselectedIcon = AkaneIcons.SearchBorder,
    ),
    DOWNLOADS(
        route = DOWNLOADS_ROUTE,
        label = "Загрузки",
        selectedIcon = AkaneIcons.Library,
        unselectedIcon = AkaneIcons.LibraryBorder,
    ),
}
