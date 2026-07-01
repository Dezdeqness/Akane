package com.dezdeqness.videoplayer.core.player.feature.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.buttons.AppSecondaryButton
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.composables.slider.formatDuration
import com.dezdeqness.videoplayer.core.player.feature.ControlSlot
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.UiFeature
import com.dezdeqness.views.contract.repository.ViewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MS_IN_SECOND = 1_000L
private const val PROMPT_DURATION_MS = 10_000L

class ResumePromptFeature(
    private val viewsRepository: ViewsRepository,
    private val releaseId: Long,
) : UiFeature {

    override val key: FeatureKey = FeatureKey.ResumePrompt
    override val slots: Set<ControlSlot> = setOf(ControlSlot.OverlayCenter)

    private var playerContext: PlayerContext? = null
    private val target = MutableStateFlow<ResumeTarget?>(null)
    private val shownEpisodes = mutableSetOf<String>()

    lateinit var isPromptVisible: StateFlow<Boolean>
        private set

    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context
        isPromptVisible = target.map { it != null }.stateIn(scope, SharingStarted.Eagerly, false)

        scope.launch {
            context.currentItem
                .map { it?.id }
                .distinctUntilChanged()
                .collect { episodeId ->
                    target.value = null
                    if (episodeId == null || episodeId in shownEpisodes) return@collect

                    val saved = viewsRepository.getReleaseTimecodes(releaseId)
                        .getOrNull()
                        ?.firstOrNull { it.releaseEpisodeId == episodeId && !it.isWatched && it.timeMs > 0L }
                        ?: return@collect

                    shownEpisodes += episodeId
                    target.value = ResumeTarget(episodeId = episodeId, positionMs = saved.timeMs)
                }
        }

        scope.launch {
            target.collectLatest { current ->
                if (current != null) {
                    delay(PROMPT_DURATION_MS)
                    if (target.value?.episodeId == current.episodeId) {
                        target.value = null
                    }
                }
            }
        }
    }

    override fun dispose() {
        playerContext = null
    }

    @Composable
    override fun Content(slot: ControlSlot) {
        val context = playerContext ?: return
        val current by target.collectAsStateOnLifecycle()

        AnimatedVisibility(
            visible = current != null,
            enter = slideInVertically(animationSpec = tween(300)) { it } + fadeIn(tween(300)),
            exit = slideOutVertically(animationSpec = tween(300)) { it } + fadeOut(tween(300)),
        ) {
            val resumeTarget = current ?: return@AnimatedVisibility
            AppSecondaryButton(
                title = "Продолжить с ${formatDuration(resumeTarget.positionMs / MS_IN_SECOND)}",
                onClick = {
                    context.seekTo(resumeTarget.positionMs)
                    target.value = null
                },
                modifier = Modifier.padding(16.dp),
            )
        }
    }

    private data class ResumeTarget(
        val episodeId: String,
        val positionMs: Long,
    )
}
