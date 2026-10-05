package com.dezdeqness.videoplayer.core.player

import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dezdeqness.videoplayer.core.player.feature.gesture.PlayerWindowHolder

@Composable
actual fun PlayerWindowEffect() {
    val window = LocalActivity.current?.window ?: return

    DisposableEffect(window) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        val previousLightStatusBars = insetsController.isAppearanceLightStatusBars
        val previousLightNavigationBars = insetsController.isAppearanceLightNavigationBars
        val previousBarsBehavior = insetsController.systemBarsBehavior

        val previousBrightness = window.attributes.screenBrightness
        val previousCutoutMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode
        } else {
            null
        }

        insetsController.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        PlayerWindowHolder.attach(window)

        onDispose {
            PlayerWindowHolder.detach()
            insetsController.apply {
                show(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = previousBarsBehavior
                isAppearanceLightStatusBars = previousLightStatusBars
                isAppearanceLightNavigationBars = previousLightNavigationBars
            }
            window.attributes = window.attributes.apply {
                screenBrightness = previousBrightness
                if (previousCutoutMode != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode = previousCutoutMode
                }
            }
        }
    }
}
