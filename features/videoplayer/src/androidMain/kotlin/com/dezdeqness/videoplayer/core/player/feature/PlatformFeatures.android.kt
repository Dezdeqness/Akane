package com.dezdeqness.videoplayer.core.player.feature

import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import com.dezdeqness.videoplayer.core.player.feature.raw.AutoHideFeature
import com.dezdeqness.videoplayer.core.player.feature.ui.PlaylistFeature
import com.dezdeqness.videoplayer.core.player.feature.ui.QualityFeature
import com.dezdeqness.videoplayer.core.player.feature.ui.ScreenLockFeature
import com.dezdeqness.videoplayer.core.player.feature.ui.SkipFeature
import com.dezdeqness.videoplayer.core.player.feature.ui.SpeedFeature

actual fun VideoPlayerManager.installPlatformFeatures() {
    installFeatures(
        AutoHideFeature(),
        SpeedFeature(),
        QualityFeature(),
        PlaylistFeature(),
        ScreenLockFeature(),
        SkipFeature(),
    )
}
