package com.dezdeqness.designsystem

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun SystemBarIcons(lightIcons: Boolean) {
    val window = LocalView.current.context.findActivity()?.window ?: return
    val stack = LocalSystemBarIconsStack.current
    val request = remember { SystemBarIconsStack.Request(lightIcons) }

    DisposableEffect(window, stack, request) {
        stack.push(request)
        window.applySystemBarIcons(stack)
        onDispose {
            stack.pop(request)
            window.applySystemBarIcons(stack)
        }
    }
    SideEffect {
        if (request.lightIcons != lightIcons) {
            request.lightIcons = lightIcons
            window.applySystemBarIcons(stack)
        }
    }
}

private fun Window.applySystemBarIcons(stack: SystemBarIconsStack) {
    val lightIcons = stack.lightIcons ?: return
    WindowCompat.getInsetsController(this, decorView).apply {
        isAppearanceLightStatusBars = !lightIcons
        isAppearanceLightNavigationBars = !lightIcons
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
