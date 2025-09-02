package com.dezdeqness.shared

import androidx.compose.ui.graphics.vector.ImageVector
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.feed.navigation.FEED_ROUTE
import com.dezdeqness.home.navigation.HOME_ROUTE
import com.dezdeqness.personal.navigation.PERSONAL_ROUTE

internal enum class AkaneBottomTabModel(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        route = HOME_ROUTE,
        selectedIcon = AkaneIcons.Home,
        unselectedIcon = AkaneIcons.HomeBorder,
    ),
    PERSONAL(
        route = PERSONAL_ROUTE,
        selectedIcon = AkaneIcons.Personal,
        unselectedIcon = AkaneIcons.PersonalBorder,
    ),
    SEARCH(
        route = FEED_ROUTE,
        selectedIcon = AkaneIcons.Search,
        unselectedIcon = AkaneIcons.SearchBorder,
    ),
}
