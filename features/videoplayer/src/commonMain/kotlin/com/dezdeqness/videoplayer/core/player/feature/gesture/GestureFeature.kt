package com.dezdeqness.videoplayer.core.player.feature.gesture

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GestureFeature(
    private val brightness: BrightnessController,
) : UiFeature {

    override val key: FeatureKey = FeatureKey.Gestures
    override val slots: Set<ControlSlot> = setOf(ControlSlot.OverlayCenter)

    private var playerContext: PlayerContext? = null
    private var scope: CoroutineScope? = null

    private val _hud = MutableStateFlow<GestureHud?>(null)
    val hud: StateFlow<GestureHud?> = _hud.asStateFlow()

    private var volume = 1f
    private var brightnessValue = 0.5f

    private var draggingSide: Side? = null
    private var hideJob: Job? = null

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
        this.scope = scope
        brightness.onEnter()
    }

    override fun dispose() {
        brightness.onExit()
        hideJob?.cancel()
        playerContext = null
        scope = null
        _hud.value = null
    }

    override fun modifier(): Modifier = Modifier.pointerInput(Unit) {
        val width = size.width
        val height = size.height
        detectVerticalDragGestures(
            onDragStart = { offset ->
                hideJob?.cancel()
                draggingSide = if (offset.x < width / 2f) Side.Brightness else Side.Volume
                if (draggingSide == Side.Brightness) {
                    brightnessValue = brightness.get().coerceIn(0f, 1f)
                }
                playerContext?.pauseAutoHide()
                publishHud()
            },
            onVerticalDrag = { _, dragAmount ->
                if (height <= 0) return@detectVerticalDragGestures
                val delta = -dragAmount / height
                when (draggingSide) {
                    Side.Brightness -> {
                        brightnessValue = (brightnessValue + delta).coerceIn(0f, 1f)
                        brightness.set(brightnessValue)
                    }
                    Side.Volume -> {
                        volume = (volume + delta).coerceIn(0f, 1f)
                        playerContext?.setVolume(volume)
                    }
                    null -> Unit
                }
                publishHud()
            },
            onDragEnd = { endDrag() },
            onDragCancel = { endDrag() },
        )
    }

    private fun endDrag() {
        draggingSide = null
        playerContext?.resumeAutoHide()
        hideJob?.cancel()
        hideJob = scope?.launch {
            delay(HIDE_DELAY_MS)
            _hud.value = null
        }
    }

    private fun publishHud() {
        _hud.value = when (draggingSide) {
            Side.Brightness -> GestureHud.Brightness(brightnessValue)
            Side.Volume -> GestureHud.Volume(volume)
            null -> null
        }
    }

    @Composable
    override fun Content(slot: ControlSlot) {
        if (slot != ControlSlot.OverlayCenter) return
        val current by hud.collectAsStateOnLifecycle()

        var lastValue by remember { mutableStateOf<GestureHud?>(null) }
        LaunchedEffect(current) { current?.let { lastValue = it } }

        AnimatedVisibility(
            visible = current != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            val value = current ?: lastValue ?: return@AnimatedVisibility
            Column(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = when (value) {
                        is GestureHud.Brightness -> when {
                            value.value < BRIGHTNESS_LOW -> AkaneIcons.BrightnessLow
                            value.value < BRIGHTNESS_MEDIUM -> AkaneIcons.BrightnessMedium
                            else -> AkaneIcons.Brightness
                        }
                        is GestureHud.Volume -> if (value.value == 0f) AkaneIcons.VolumeOff else AkaneIcons.VolumeUp
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = "${(value.value * 100).toInt()}%",
                    color = Color.White,
                )
            }
        }
    }

    private enum class Side { Brightness, Volume }

    private companion object {
        const val BRIGHTNESS_LOW = 0.33f
        const val BRIGHTNESS_MEDIUM = 0.66f
        const val HIDE_DELAY_MS = 1000L
    }
}

sealed interface GestureHud {
    val value: Float

    data class Brightness(override val value: Float) : GestureHud
    data class Volume(override val value: Float) : GestureHud
}
