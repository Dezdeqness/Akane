package com.dezdeqness.videoplayer.core.player.feature

import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import com.dezdeqness.videoplayer.core.player.feature.raw.AutoHideFeature
import com.dezdeqness.videoplayer.core.player.feature.raw.KeyboardShortcutsFeature

actual fun VideoPlayerManager.installPlatformFeatures() {
    installFeatures(
        AutoHideFeature(),
        KeyboardShortcutsFeature(),
    )
}
