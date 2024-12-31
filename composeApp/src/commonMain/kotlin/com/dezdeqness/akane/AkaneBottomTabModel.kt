package com.dezdeqness.akane

import androidx.compose.ui.graphics.vector.ImageVector
import com.dezdeqness.designsystem.icons.AkaneIcons


internal enum class AkaneBottomTabModel(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        selectedIcon = AkaneIcons.Home,
        unselectedIcon = AkaneIcons.HomeBorder,
    ),
    PERSONAL(
        selectedIcon = AkaneIcons.Personal,
        unselectedIcon = AkaneIcons.PersonalBorder,
    ),
    SEARCH(
        selectedIcon = AkaneIcons.Search,
        unselectedIcon = AkaneIcons.SearchBorder,
    ),
    PROFILE(
        selectedIcon = AkaneIcons.Profile,
        unselectedIcon = AkaneIcons.ProfileBoarder,
    ),
}