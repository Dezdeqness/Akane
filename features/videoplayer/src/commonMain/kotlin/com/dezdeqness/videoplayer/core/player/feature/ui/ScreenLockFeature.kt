package com.dezdeqness.videoplayer.core.player.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import kotlinx.coroutines.CoroutineScope

class ScreenLockFeature : UiFeature {

    override val key: FeatureKey = FeatureKey.ScreenLock
    override val slots: Set<ControlSlot> = setOf(ControlSlot.CenterEnd)

    private var playerContext: PlayerContext? = null

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
    }

    override fun dispose() {
        playerContext = null
    }

    fun toggleLock() {
        val context = playerContext ?: return
        val newLocked = !context.isLocked.value
        context.setLocked(newLocked)
    }

    @Composable
    override fun Content(slot: ControlSlot) {
        val context = playerContext ?: return

        val isLocked by context.isLocked.collectAsStateOnLifecycle()

        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = ::toggleLock),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (isLocked) AkaneIcons.Unlocked else AkaneIcons.Locked,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}
