package com.dezdeqness.foundation.config

import androidx.compose.runtime.Composable
import java.awt.Toolkit

@Composable
actual fun getDeviceConfiguration(): DeviceConfiguration {
    val size = Toolkit.getDefaultToolkit().screenSize
    val screenWidthPx = size.width
    val screenHeightPx = size.height

    val aspect = if (screenWidthPx > 0 && screenHeightPx > 0)
        screenWidthPx.toFloat() / screenHeightPx
    else null

    return DeviceConfiguration(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        aspectRatio = aspect,
        isPortrait = aspect?.let { it < 1f } ?: false
    )
}