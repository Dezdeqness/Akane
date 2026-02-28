package com.dezdeqness.videoplayer.core.player.feature.gesture

import android.app.Activity
import com.dezdeqness.videoplayer.engine.feature.gesture.BrightnessController
import java.lang.ref.WeakReference

class AndroidBrightnessController() : BrightnessController {

    private var activityRef: WeakReference<Activity>? = null

    fun bind(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    fun unbind() {
        activityRef = null
    }

    override fun adjust(delta: Float) {
        val activity = activityRef?.get() ?: return
        val lp = activity.window.attributes
        val current = if (lp.screenBrightness < 0f) 0.5f else lp.screenBrightness
        lp.screenBrightness = (current + delta).coerceIn(0.01f, 1f)
        activity.window.attributes = lp
    }
}
