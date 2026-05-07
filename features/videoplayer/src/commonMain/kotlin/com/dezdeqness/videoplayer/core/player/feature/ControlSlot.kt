package com.dezdeqness.videoplayer.core.player.feature

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ControlSlot {
    data object TopStart : ControlSlot
    data object TopEnd : ControlSlot
    data object Center : ControlSlot
    data object CenterEnd : ControlSlot
    data object BottomStart : ControlSlot
    data object BottomEnd : ControlSlot
    data object BottomActionsStart : ControlSlot
    data object BottomActionsEnd : ControlSlot
    data object Overlay : ControlSlot
    data object SidePanel  : ControlSlot
}
