package com.dezdeqness.designsystem.layouts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class LayoutType {
    Mobile,
    Tablet,
    Desktop,
}

private val TabletWidth = 600.dp
private val DesktopWidth = 1024.dp

fun adaptiveLayoutType(width: Dp): LayoutType {
    return when {
        width < TabletWidth -> LayoutType.Mobile
        width < DesktopWidth -> LayoutType.Tablet
        else -> LayoutType.Desktop
    }
}

@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val type = adaptiveLayoutType(maxWidth)

        CompositionLocalProvider(LocalLayoutType provides type) {
            content()
        }
    }
}

val LocalLayoutType = staticCompositionLocalOf<LayoutType> {
    error("No LayoutType provided")
}
