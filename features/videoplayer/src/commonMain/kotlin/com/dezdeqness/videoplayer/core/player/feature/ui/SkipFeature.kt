package com.dezdeqness.videoplayer.core.player.feature.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.buttons.AppSecondaryButton
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.data.SkipRange
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private const val PRE_SHOW_MS = 5_000L

class SkipFeature : UiFeature {

    override val key: FeatureKey = FeatureKey.Skip
    override val slots: Set<ControlSlot> = setOf(ControlSlot.Overlay)

    private var playerContext: PlayerContext? = null
    private val _openingRange = MutableStateFlow<SkipRange?>(null)
    private val _endingRange = MutableStateFlow<SkipRange?>(null)

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
        scope.launch {
            context.currentItem.collect { item ->
                _openingRange.value = item?.opening
                _endingRange.value = item?.ending
            }
        }
    }

    override fun dispose() {
        playerContext = null
    }

    @Composable
    override fun Content(slot: ControlSlot) {
        val context = playerContext ?: return
        val state by context.playerState.collectAsStateOnLifecycle()
        val openingRange by _openingRange.collectAsStateOnLifecycle()
        val endingRange by _endingRange.collectAsStateOnLifecycle()
        val pos = state.position

        val skipState by remember(openingRange, endingRange) {
            derivedStateOf {
                resolveSkipState(pos, openingRange, endingRange)
            }
        }

        val currentSkip = resolveSkipState(pos, openingRange, endingRange)
        AnimatedVisibility(
            visible = currentSkip != null,
            enter = slideInHorizontally(tween(300)) { it },
            exit = slideOutHorizontally(tween(300)) { it },
        ) {
            val label = currentSkip?.first ?: skipState?.first ?: return@AnimatedVisibility
            val targetMs = currentSkip?.second ?: skipState?.second ?: return@AnimatedVisibility

            AppSecondaryButton(
                title = label,
                onClick = { context.seekTo(targetMs) },
                modifier = Modifier.padding(16.dp),
            )
        }
    }

    private fun resolveSkipState(
        pos: Long,
        openingRange: SkipRange?,
        endingRange: SkipRange?,
    ): Pair<String, Long>? {
        if (openingRange != null &&
            pos in (openingRange.startMs - PRE_SHOW_MS)..openingRange.endMs
        ) {
            return "Пропустить опенинг" to openingRange.endMs
        }
        if (endingRange != null &&
            pos in (endingRange.startMs - PRE_SHOW_MS)..endingRange.endMs
        ) {
            return "Пропустить эндинг" to endingRange.endMs
        }
        return null
    }
}
