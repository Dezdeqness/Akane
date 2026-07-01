package com.dezdeqness.videoplayer.core.player.feature

interface FeatureKey {
    data object AutoHideControls : FeatureKey
    data object Gestures : FeatureKey
    data object KeyboardShortcuts : FeatureKey
    data object Quality : FeatureKey
    data object Speed : FeatureKey
    data object AspectRatio : FeatureKey
    data object ScreenLock : FeatureKey
    data object Skip : FeatureKey
    data object Screenshot : FeatureKey
    data object Playlist : FeatureKey
    data object Timecode : FeatureKey
    data object ResumePrompt : FeatureKey

    data class Custom(val id: String) : FeatureKey
}
