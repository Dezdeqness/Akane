package com.dezdeqness.videoplayer.core

import androidx.compose.runtime.Composable

@Composable
actual fun getDeviceConfiguration(): DeviceConfiguration {
    val screen = UIScreen.mainScreen
    val scale = screen.scale
    val orientation = UIDevice.currentDevice.orientation
    return object : DeviceConfiguration {
        override val screenWidthPx: Int = (screen.bounds.size.width * scale).toInt()
        override val screenHeightPx: Int = (screen.bounds.size.height * scale).toInt()
        override val aspectRatio: Float? = if (screenWidthPx > 0 && screenHeightPx > 0) {
            screenWidthPx.toFloat() / screenHeightPx
        } else null
        override val isPortrait: Boolean = orientation == UIDeviceOrientationPortrait ||
                orientation == UIDeviceOrientationPortraitUpsideDown
    }
}