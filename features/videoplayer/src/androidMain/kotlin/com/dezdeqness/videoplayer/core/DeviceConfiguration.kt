package com.dezdeqness.videoplayer.core

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun getDeviceConfiguration(): DeviceConfiguration {
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp * configuration.densityDpi / 160
    val screenHeightPx = configuration.screenHeightDp * configuration.densityDpi / 160

    return DeviceConfiguration(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        aspectRatio = if (screenWidthPx > 0 && screenHeightPx > 0) {
            screenWidthPx.toFloat() / screenHeightPx
        } else null,
        isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    )
}
