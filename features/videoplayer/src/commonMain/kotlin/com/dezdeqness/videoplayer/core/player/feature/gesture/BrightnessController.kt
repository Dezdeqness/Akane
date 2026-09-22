package com.dezdeqness.videoplayer.core.player.feature.gesture

interface BrightnessController {
    fun get(): Float
    fun set(value: Float)

    fun onEnter() {}
    fun onExit() {}
}
