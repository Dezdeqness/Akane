package com.dezdeqness.analytics.core

import platform.Foundation.NSLocale
import platform.Foundation.autoupdatingCurrentLocale
import platform.Foundation.localeIdentifier
import platform.UIKit.UIDevice

actual fun readPlatformSystemInfo(): PlatformSystemInfo {
    val locale = NSLocale.autoupdatingCurrentLocale.localeIdentifier.replace('_', '-')
    val device = UIDevice.currentDevice

    return PlatformSystemInfo(
        locale = locale,
        osName = device.systemName,
        osVersion = device.systemVersion,
        deviceModel = device.model,
    )
}
