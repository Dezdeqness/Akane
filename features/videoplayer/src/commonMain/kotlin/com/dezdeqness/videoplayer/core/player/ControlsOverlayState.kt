package com.dezdeqness.videoplayer.core.player

enum class ControlsOverlayState(
    val isLocked: Boolean,
    val controlsVisible: Boolean,
    val lockedControlsVisible: Boolean,
) {
    UnlockedVisible(
        isLocked = false,
        controlsVisible = true,
        lockedControlsVisible = false,
    ),
    UnlockedHidden(
        isLocked = false,
        controlsVisible = false,
        lockedControlsVisible = false,
    ),
    LockedVisible(
        isLocked = true,
        controlsVisible = false,
        lockedControlsVisible = true,
    ),
    LockedHidden(
        isLocked = true,
        controlsVisible = false,
        lockedControlsVisible = false,
    ),
}
