package com.dezdeqness.shared

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.downloads.navigation.DownloadsRoute
import com.dezdeqness.feed.navigation.FeedRoute
import com.dezdeqness.home.navigation.HomeRoute
import com.dezdeqness.personal.navigation.PersonalRoute

internal enum class AkaneBottomTabModel(
    val key: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        key = HomeRoute,
        label = "Главная",
        selectedIcon = AkaneIcons.Home,
        unselectedIcon = AkaneIcons.HomeBorder,
    ),
    PERSONAL(
        key = PersonalRoute,
        label = "Сохранённое",
        selectedIcon = AkaneIcons.Personal,
        unselectedIcon = AkaneIcons.PersonalBorder,
    ),
    SEARCH(
        key = FeedRoute,
        label = "Поиск",
        selectedIcon = AkaneIcons.Search,
        unselectedIcon = AkaneIcons.SearchBorder,
    ),
    DOWNLOADS(
        key = DownloadsRoute,
        label = "Загрузки",
        selectedIcon = AkaneIcons.Library,
        unselectedIcon = AkaneIcons.LibraryBorder,
    ),
}
