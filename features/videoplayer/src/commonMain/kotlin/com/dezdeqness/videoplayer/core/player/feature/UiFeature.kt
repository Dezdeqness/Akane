package com.dezdeqness.videoplayer.core.player.feature

import androidx.compose.runtime.Composable

interface UiFeature : PlayerFeature {

    val slots: Set<ControlSlot>

    @Composable
    fun Content(slot: ControlSlot)
}
