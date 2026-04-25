package com.dezdeqness.home.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HomeWideSectionLayout(
    val featuredCardHeight: Dp,
    val sidebarWidth: Dp,
    val cardWidth: Dp,
    val imageHeight: Dp,
    val itemSpacing: Dp,
    val sectionTitleSize: TextUnit,
    val cardTitleSize: TextUnit,
)

@Composable
fun rememberHomeWideSectionLayout(maxWidth: Dp): HomeWideSectionLayout {
    return remember(maxWidth) {
        when {
            maxWidth >= 1400.dp -> HomeWideSectionLayout(
                featuredCardHeight = 452.dp,
                sidebarWidth = 318.dp,
                cardWidth = 200.dp,
                imageHeight = 272.dp,
                itemSpacing = 20.dp,
                sectionTitleSize = 24.sp,
                cardTitleSize = 16.sp,
            )
            maxWidth >= 1100.dp -> HomeWideSectionLayout(
                featuredCardHeight = 412.dp,
                sidebarWidth = 288.dp,
                cardWidth = 188.dp,
                imageHeight = 256.dp,
                itemSpacing = 18.dp,
                sectionTitleSize = 22.sp,
                cardTitleSize = 15.sp,
            )
            else -> HomeWideSectionLayout(
                featuredCardHeight = 372.dp,
                sidebarWidth = 260.dp,
                cardWidth = 176.dp,
                imageHeight = 236.dp,
                itemSpacing = 16.dp,
                sectionTitleSize = 20.sp,
                cardTitleSize = 14.sp,
            )
        }
    }
}
