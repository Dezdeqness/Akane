package com.dezdeqness.videoplayer.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun rememberFullScreenState(
    initialNavigationBarVisibility: SystemBarsVisibility = SystemBarsVisibility.Visible,
    initialStatusBarVisibility: SystemBarsVisibility = SystemBarsVisibility.Visible,
): FullScreenState {
    return remember {
        FullScreenState(
            initialNavigationBarVisibility,
            initialStatusBarVisibility
        )
    }
}

enum class SystemBarsVisibility {
    Visible,
    Gone
}

class FullScreenState(
    initialNavigationBarVisibility: SystemBarsVisibility,
    initialStatusBarVisibility: SystemBarsVisibility
) {

    private var navigationBarVisibility by mutableStateOf(initialNavigationBarVisibility)
    private var statusBarVisibility by mutableStateOf(initialStatusBarVisibility)

    val isNavigationBarVisible: Boolean
        get() = navigationBarVisibility == SystemBarsVisibility.Visible

    val isStatusBarVisible: Boolean
        get() = statusBarVisibility == SystemBarsVisibility.Visible

    val isSystemBarVisible: Boolean
        get() = navigationBarVisibility == SystemBarsVisibility.Visible && statusBarVisibility == SystemBarsVisibility.Visible

    fun showNavigationBar() {
        navigationBarVisibility = SystemBarsVisibility.Visible
    }

    fun showStatusBar() {
        statusBarVisibility = SystemBarsVisibility.Visible
    }

    fun showSystemBar() {
        navigationBarVisibility = SystemBarsVisibility.Visible
        statusBarVisibility = SystemBarsVisibility.Visible
    }

    fun hideNavigationBar() {
        navigationBarVisibility = SystemBarsVisibility.Gone
    }

    fun hideStatusBar() {
        statusBarVisibility = SystemBarsVisibility.Gone
    }

    fun hideSystemBar() {
        navigationBarVisibility = SystemBarsVisibility.Gone
        statusBarVisibility = SystemBarsVisibility.Gone
    }

}
