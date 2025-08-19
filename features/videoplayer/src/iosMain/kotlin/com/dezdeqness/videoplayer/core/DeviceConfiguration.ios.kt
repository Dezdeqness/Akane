package com.dezdeqness.videoplayer.core

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun getDeviceConfiguration(): DeviceConfiguration {
    val screen = UIScreen.mainScreen
    val scale = screen.scale
    val orientation = UIDevice.currentDevice.orientation

    val screenWidthPx: Int = (screen.bounds.useContents { size.width } * scale).toInt()
    val screenHeightPx: Int = (screen.bounds.useContents { size.height } * scale).toInt()
    val aspectRatio: Float? = if (screenWidthPx > 0 && screenHeightPx > 0) {
        screenWidthPx.toFloat() / screenHeightPx
    } else null
    val isPortrait: Boolean = orientation == UIDeviceOrientation.UIDeviceOrientationPortrait ||
            orientation == UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown
    return DeviceConfiguration(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        aspectRatio = aspectRatio,
        isPortrait = isPortrait,
    )
}
