package com.dezdeqness.videoplayer.core.player.feature.raw

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.PlayerFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AutoHideFeature(
    private val timeoutMs: Long = 5_000L,
) : PlayerFeature {

    override val key: FeatureKey = FeatureKey.AutoHideControls

    private var playerContext: PlayerContext? = null
    private var featureScope: CoroutineScope? = null
    private var timerJob: Job? = null

    private var isPointerDown = false

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
        featureScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        featureScope?.launch {
            context.playerState
                .map { it.isPlaying }
                .distinctUntilChanged()
                .collect { isPlaying ->
                    timerJob?.cancel()
                    if (context.isLocked.value) {
                        if (isPlaying && context.lockedControlsVisible.value) {
                            restartTimer()
                        }
                    } else {
                        context.showControls()
                        if (isPlaying) restartTimer()
                    }
                }
        }
    }

    override fun modifier(): Modifier = Modifier
        .pointerInput(Unit) {
            awaitPointerEventScope {
                var wasPressed = false
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    val isPressed = event.changes.any { it.pressed }

                    if (!wasPressed && isPressed) {
                        isPointerDown = true
                        val context = playerContext ?: run { wasPressed = isPressed; continue }

                        val anyConsumed = event.changes.any { it.isConsumed }
                        if (anyConsumed) {
                            timerJob?.cancel()
                            if (context.playerState.value.isPlaying) {
                                restartTimer()
                            }
                            wasPressed = isPressed
                            continue
                        }

                        if (context.isLocked.value) {
                            if (context.lockedControlsVisible.value) {
                                context.hideControls()
                                timerJob?.cancel()
                            } else {
                                context.showControls()
                                timerJob?.cancel()
                                if (context.playerState.value.isPlaying) {
                                    restartTimer()
                                }
                            }
                            wasPressed = isPressed
                            continue
                        }
                        if (context.controlsVisible.value) {
                            context.hideControls()
                            timerJob?.cancel()
                        } else {
                            context.showControls()
                            timerJob?.cancel()
                        }
                    }

                    if (wasPressed && !isPressed) {
                        isPointerDown = false
                        val context = playerContext ?: run { wasPressed = isPressed; continue }
                        if (context.playerState.value.isPlaying) {
                            restartTimer()
                        }
                    }

                    wasPressed = isPressed
                }
            }
        }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    if (event.type == PointerEventType.Move) {
                        val context = playerContext ?: continue
                        if (context.isLocked.value) continue
                        if (!context.controlsVisible.value) {
                            context.showControls()
                        }
                        timerJob?.cancel()
                        if (context.playerState.value.isPlaying) {
                            restartTimer()
                        }
                    }
                }
            }
        }

    private fun restartTimer() {
        val context = playerContext ?: return
        timerJob?.cancel()
        timerJob = featureScope?.launch {
            delay(timeoutMs)

            if (isPointerDown) {
                restartTimer()
                return@launch
            }

            if (context.autoHidePaused.value) {
                context.autoHidePaused.collect { paused ->
                    if (!paused) {
                        restartTimer()
                        return@collect
                    }
                }
            }
            if (context.playerState.value.isPlaying) {
                context.hideControls()
            }
        }
    }

    override fun dispose() {
        timerJob?.cancel()
        featureScope?.cancel()
        featureScope = null
        playerContext = null
    }
}
