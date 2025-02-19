package com.dezdeqness.videoplayer.core

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


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

@Composable
fun InstallFullScreenState(state: FullScreenState = rememberFullScreenState()) {

    val mState by rememberUpdatedState(newValue = state)

    val context = LocalContext.current

    val insetsController = remember(context) {
        val window = run {
            // Get activity from context
            while (context is ContextWrapper) {
                if (context is Activity) return@run context
                return@run context.baseContext as Activity
            }

            null
        }?.window ?: return@remember null

        WindowCompat.getInsetsController(window, window.decorView)
    }

    DisposableEffect(
        state.isNavigationBarVisible,
        state.isStatusBarVisible,
        state.isSystemBarVisible
    ) {
        insetsController?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (mState.isNavigationBarVisible) show(WindowInsetsCompat.Type.navigationBars())
            else hide(WindowInsetsCompat.Type.navigationBars())

            if (mState.isStatusBarVisible) show(WindowInsetsCompat.Type.statusBars())
            else hide(WindowInsetsCompat.Type.statusBars())
        }

        onDispose {}
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
