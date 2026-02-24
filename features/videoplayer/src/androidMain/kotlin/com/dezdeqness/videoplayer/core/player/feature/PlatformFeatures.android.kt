package com.dezdeqness.videoplayer.core.player.feature

import com.dezdeqness.videoplayer.core.player.feature.autohide.AutoHideControlsFeature

actual fun platformFeatures(): List<PlayerFeature> = listOf(
    AutoHideControlsFeature(),
)
