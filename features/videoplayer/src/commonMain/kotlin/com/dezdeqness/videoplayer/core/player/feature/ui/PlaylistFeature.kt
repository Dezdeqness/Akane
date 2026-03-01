package com.dezdeqness.videoplayer.core.player.feature.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.composables.bottomsheet.PlaylistBottomSheet
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class PlaylistFeature : UiFeature {

    override val key: FeatureKey = FeatureKey.Playlist
    override val slots: Set<ControlSlot> = setOf(ControlSlot.BottomEnd)

    private var playerContext: PlayerContext? = null
    private val _sheetVisible = MutableStateFlow(false)

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
    }

    override fun dispose() {
        playerContext = null
    }

    @Composable
    override fun Content(slot: ControlSlot) {
        val context = playerContext ?: return
        val playlist by context.playlist.collectAsStateOnLifecycle()
        val currentItem by context.currentItem.collectAsStateOnLifecycle()
        val sheetVisible by _sheetVisible.collectAsStateOnLifecycle()

        if (playlist.size <= 1) return

        AppIconButton(
            icon = AkaneIcons.Menu,
            tint = Color.White,
            onClick = {
                _sheetVisible.value = true
                context.pauseAutoHide()
            },
        )

        if (sheetVisible) {
            PlaylistBottomSheet(
                items = playlist,
                currentItemId = currentItem?.id,
                onSelected = { id ->
                    context.selectItem(id)
                    _sheetVisible.value = false
                    context.resumeAutoHide()
                },
                onDismiss = {
                    _sheetVisible.value = false
                    context.resumeAutoHide()
                },
            )
        }
    }
}
