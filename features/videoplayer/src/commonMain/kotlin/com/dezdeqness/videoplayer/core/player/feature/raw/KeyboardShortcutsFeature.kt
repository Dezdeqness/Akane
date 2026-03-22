package com.dezdeqness.videoplayer.core.player.feature.raw

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.PlayerFeature
import kotlinx.coroutines.CoroutineScope

class KeyboardShortcutsFeature : PlayerFeature {

    override val key: FeatureKey = FeatureKey.KeyboardShortcuts

    private var playerContext: PlayerContext? = null

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
    }

    override fun modifier(): Modifier = Modifier.composed {
        val context = playerContext ?: return@composed this

        val focusRequester = remember { FocusRequester() }
        val controlsVisible by context.controlsVisible.collectAsState()

        LaunchedEffect(controlsVisible) {
            if (!controlsVisible) {
                focusRequester.requestFocus()
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        this
            .focusRequester(focusRequester)
            .focusTarget()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                val context = playerContext ?: return@onPreviewKeyEvent false

                when (event.key) {
                    Key.Spacebar -> {
                        if (context.playerState.value.isPlaying) context.pause() else context.play()
                        true
                    }
                    Key.DirectionRight -> { context.seekForward(); true }
                    Key.DirectionLeft -> { context.seekBack(); true }
                    else -> false
                }
            }
    }

    override fun dispose() {
        playerContext = null
    }
}