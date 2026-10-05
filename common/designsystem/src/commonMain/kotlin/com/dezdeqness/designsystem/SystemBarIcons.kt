package com.dezdeqness.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
expect fun SystemBarIcons(lightIcons: Boolean)

class SystemBarIconsStack {
    internal class Request(var lightIcons: Boolean)

    private val requests = mutableListOf<Request>()

    internal val lightIcons: Boolean?
        get() = requests.lastOrNull()?.lightIcons

    internal fun push(request: Request) {
        requests += request
    }

    internal fun pop(request: Request) {
        requests -= request
    }
}

val LocalSystemBarIconsStack = staticCompositionLocalOf<SystemBarIconsStack> {
    error("No SystemBarIconsStack provided")
}
