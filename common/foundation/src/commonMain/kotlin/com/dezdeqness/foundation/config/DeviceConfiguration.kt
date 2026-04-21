package com.dezdeqness.foundation.config

import androidx.compose.runtime.Composable

data class DeviceConfiguration(
    val screenWidthPx: Int,
    val screenHeightPx: Int,
    val aspectRatio: Float?,
    val isPortrait: Boolean,
)

@Composable
expect fun getDeviceConfiguration(): DeviceConfiguration
