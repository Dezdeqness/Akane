package com.dezdeqness.videoplayer.core.player.feature.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VolumeMixerFeature(
    initialVolume: Float = 1f,
) : UiFeature {

    override val key: FeatureKey = FeatureKey.Custom("volume-mixer")
    override val slots: Set<ControlSlot> = setOf(ControlSlot.BottomEnd)

    private val _volume = MutableStateFlow(initialVolume.coerceIn(0f, 1f))
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private var lastNonZeroVolume: Float = initialVolume.coerceIn(0f, 1f).takeIf { it > 0f } ?: 1f

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        scope.launch {
            _volume.collect { value -> context.setVolume(value) }
        }
    }

    override fun dispose() = Unit

    private fun setVolume(value: Float) {
        val clamped = value.coerceIn(0f, 1f)
        if (clamped > 0f) lastNonZeroVolume = clamped
        _volume.value = clamped
    }

    private fun toggleMute() {
        _volume.value = if (_volume.value > 0f) 0f else lastNonZeroVolume
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(slot: ControlSlot) {
        if (slot != ControlSlot.BottomEnd) return

        val current by volume.collectAsStateOnLifecycle()
        val interactionSource = remember { MutableInteractionSource() }
        val hovered by interactionSource.collectIsHoveredAsState()

        Row(
            modifier = Modifier
                .hoverable(interactionSource)
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIconButton(
                icon = if (current == 0f) AkaneIcons.VolumeOff else AkaneIcons.VolumeUp,
                tint = Color.White,
                onClick = ::toggleMute,
            )

            AnimatedVisibility(
                visible = hovered,
                enter = expandHorizontally() + fadeIn(),
                exit = shrinkHorizontally() + fadeOut(),
            ) {
                Slider(
                    value = current,
                    onValueChange = ::setVolume,
                    valueRange = 0f..1f,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .width(120.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                    ),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                        )
                    },
                )
            }
        }
    }
}
