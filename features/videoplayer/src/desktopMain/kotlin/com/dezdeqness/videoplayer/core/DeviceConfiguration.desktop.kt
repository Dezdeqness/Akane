package com.dezdeqness.videoplayer.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.toSize
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