package com.dezdeqness.videoplayer.core.player.feature.gesture

import android.view.Window
import java.lang.ref.WeakReference

object PlayerWindowHolder {
    private var windowRef: WeakReference<Window>? = null

    fun attach(window: Window) {
        windowRef = WeakReference(window)
    }

    fun detach() {
        windowRef = null
    }

    fun window(): Window? = windowRef?.get()
}
