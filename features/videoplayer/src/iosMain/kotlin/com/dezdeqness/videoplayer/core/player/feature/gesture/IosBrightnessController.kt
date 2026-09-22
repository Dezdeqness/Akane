package com.dezdeqness.videoplayer.core.player.feature.gesture

import platform.UIKit.UIScreen

class IosBrightnessController : BrightnessController {

    private var originalBrightness: Float? = null

    override fun get(): Float = UIScreen.mainScreen.brightness.toFloat().coerceIn(0f, 1f)

    override fun set(value: Float) {
        UIScreen.mainScreen.brightness = value.coerceIn(0f, 1f).toDouble()
    }

    override fun onEnter() {
        originalBrightness = UIScreen.mainScreen.brightness.toFloat()
    }

    override fun onExit() {
        originalBrightness?.let { UIScreen.mainScreen.brightness = it.toDouble() }
        originalBrightness = null
    }
}
