package com.dezdeqness.videoplayer.core.player.feature.autohide

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.dezdeqness.videoplayer.core.player.VideoPlayerController
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

class AutoHideControlsFeature(
    private val timeoutMs: Long = 5000L,
) : PlayerFeature {

    private var scope: CoroutineScope? = null
    private var timerJob: Job? = null
    private var controller: VideoPlayerController? = null

    override fun install(controller: VideoPlayerController) {
        this.controller = controller
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        scope?.launch {
            controller.state.map { it.isPlaying }
                .distinctUntilChanged()
                .collect { playing ->
                    timerJob?.cancel()
                    controller.showControls()
                    if (playing) {
                        restartTimer()
                    }
                }
        }
    }

    override fun modifier(): Modifier = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            var wasPressed = false
            while (true) {

                val event = awaitPointerEvent(PointerEventPass.Final)
                val isPressed = event.changes.any { it.pressed }

                if (!wasPressed && isPressed) {
                    if (controller?.overlay?.value?.controlsVisible == true) {
                        controller?.hideControls()
                        timerJob?.cancel()
                    } else {
                        controller?.showControls()
                        timerJob?.cancel()
                    }
                }

                if (wasPressed && !isPressed) {
                    if (controller?.state?.value?.isPlaying == true) {
                        restartTimer()
                    }
                }

                wasPressed = isPressed
            }
        }
    }

    private fun restartTimer() {
        val controller = controller ?: return
        timerJob?.cancel()
        timerJob = scope?.launch {
            delay(timeoutMs)
            if (controller.state.value.isPlaying) {
                controller.hideControls()
            }
        }
    }

    override fun dispose() {
        timerJob?.cancel()
        scope?.cancel()
        scope = null
        controller = null
    }
}
