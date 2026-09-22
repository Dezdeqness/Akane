package com.dezdeqness.videoplayer.core.player.feature.gesture

import android.provider.Settings

class AndroidBrightnessController : BrightnessController {

    override fun get(): Float {
        val window = PlayerWindowHolder.window() ?: return systemBrightness()
        val current = window.attributes.screenBrightness
        return if (current >= 0f) current.coerceIn(0f, 1f) else systemBrightness()
    }

    override fun set(value: Float) {
        val window = PlayerWindowHolder.window() ?: return
        window.attributes = window.attributes.apply {
            screenBrightness = value.coerceIn(0f, 1f)
        }
    }

    private fun systemBrightness(): Float {
        val window = PlayerWindowHolder.window() ?: return DEFAULT_BRIGHTNESS
        return runCatching {
            val raw = Settings.System.getInt(
                window.context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
            )
            (raw / 255f).coerceIn(0f, 1f)
        }.getOrDefault(DEFAULT_BRIGHTNESS)
    }

    private companion object {
        const val DEFAULT_BRIGHTNESS = 0.5f
    }
}
