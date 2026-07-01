package com.dezdeqness.videoplayer.core.player.feature.raw

import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.feature.FeatureKey
import com.dezdeqness.videoplayer.core.player.feature.PlayerFeature
import com.dezdeqness.views.contract.model.EpisodeTimecodeEntity
import com.dezdeqness.views.contract.repository.ViewsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimecodeFeature(
    private val viewsRepository: ViewsRepository,
    private val releaseId: Long,
    dispatcher: CoroutineDispatcher,
) : PlayerFeature {

    override val key: FeatureKey = FeatureKey.Timecode

    private val saveScope = CoroutineScope(dispatcher + SupervisorJob())
    private var playerContext: PlayerContext? = null

    @OptIn(FlowPreview::class)
    override fun install(context: PlayerContext, scope: CoroutineScope) {
        playerContext = context

        scope.launch {
            while (isActive) {
                delay(SAVE_INTERVAL_MS)
                if (context.playerState.value.isPlaying) {
                    save(context)
                }
            }
        }

        scope.launch {
            context.playerState
                .map { it.isPlaying }
                .distinctUntilChanged()
                .collect { isPlaying ->
                    if (!isPlaying) {
                        save(context)
                    }
                }
        }

        scope.launch {
            context.playerState
                .filter { !it.isPlaying }
                .map { it.position }
                .distinctUntilChanged()
                .debounce(SEEK_SAVE_DEBOUNCE_MS)
                .collect { save(context) }
        }
    }

    override fun dispose() {
        playerContext?.let { context -> save(context) }
        playerContext = null
    }

    private fun save(context: PlayerContext) {
        val item = context.currentItem.value ?: return
        val state = context.playerState.value
        if (state.position <= 0L) return

        val timecode = EpisodeTimecodeEntity(
            releaseEpisodeId = item.id,
            timeMs = state.position,
            isWatched = isNearEnd(state.position, state.duration),
        )
        saveScope.launch { viewsRepository.saveTimecode(releaseId, timecode) }
    }

    private fun isNearEnd(positionMs: Long, durationMs: Long): Boolean =
        durationMs > 0L && positionMs >= durationMs * WATCHED_THRESHOLD

    private companion object {
        const val SAVE_INTERVAL_MS = 15_000L
        const val SEEK_SAVE_DEBOUNCE_MS = 1_000L
        const val WATCHED_THRESHOLD = 0.9f
    }
}
