package com.dezdeqness.videoplayer.core.player.feature.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.composables.dropdown.VideoSpeedDropdownMenu
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PlaybackSpeed(val multiplier: Float, val label: String) {
    X0_5(0.5f, "0.5x"),
    X0_75(0.75f, "0.75x"),
    X1(1f, "1x"),
    X1_25(1.25f, "1.25x"),
    X1_5(1.5f, "1.5x"),
    X1_75(1.75f, "1.75x"),
    X2(2f, "2x"),
}

class SpeedFeature(
    initialSpeed: PlaybackSpeed = PlaybackSpeed.X1,
) : UiFeature {

    override val key: FeatureKey = FeatureKey.Speed
    override val slots: Set<ControlSlot> = setOf(ControlSlot.BottomEnd)

    private var playerContext: PlayerContext? = null

    private val _selectedSpeed = MutableStateFlow(initialSpeed)
    val selectedSpeed: StateFlow<PlaybackSpeed> = _selectedSpeed.asStateFlow()

    private val _menuVisible = MutableStateFlow(false)

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
        scope.launch {
            _selectedSpeed
                .collect { speed -> context.setSpeed(speed.multiplier) }
        }
    }

    override fun dispose() {
        playerContext = null
    }

    fun selectSpeed(speed: PlaybackSpeed) {
        _selectedSpeed.value = speed
        _menuVisible.value = false
        playerContext?.resumeAutoHide()
    }

    @Composable
    override fun Content(slot: ControlSlot) {
        val speed by selectedSpeed.collectAsStateOnLifecycle()
        val menuVisible by _menuVisible.collectAsStateOnLifecycle()

        Box {
            AppIconButton(
                onClick = {
                    _menuVisible.value = true
                    playerContext?.pauseAutoHide()
                },
                contentColor = Color.Black.copy(alpha = 0.5f)
            ) {
                Text(speed.label, color = Color.White)
            }
            VideoSpeedDropdownMenu(
                isExpanded = menuVisible,
                currentSpeed = speed,
                onSpeedChange = ::selectSpeed,
                onDismiss = {
                    _menuVisible.value = false
                    playerContext?.resumeAutoHide()
                },
            )
        }
    }
}
