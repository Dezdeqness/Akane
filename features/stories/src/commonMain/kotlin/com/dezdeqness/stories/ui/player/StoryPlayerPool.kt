package com.dezdeqness.stories.ui.player

import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import com.dezdeqness.videoplayer.core.player.provider.VideoPlayerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StoryPlayerPool(
    playerProvider: VideoPlayerProvider,
) {

    data class Request(
        val episodeId: String,
        val url: String,
        val startPositionMs: Long,
    )

    private data class Slot(val manager: VideoPlayerManager) {
        var episodeId: String? = null
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val slots = List(POOL_SIZE) { Slot(VideoPlayerManager(playerProvider.createPreview(), scope)) }

    private val _players = MutableStateFlow<Map<String, VideoPlayerManager>>(emptyMap())

    val players: StateFlow<Map<String, VideoPlayerManager>> = _players.asStateFlow()

    fun acquire(active: Request, keep: Set<String>): VideoPlayerManager {
        slots.firstOrNull { it.episodeId == active.episodeId }?.let { return it.manager }

        val slot = pickFreeSlot(protected = keep)
        load(slot, active)
        publish()
        return slot.manager
    }

    fun release() {
        slots.forEach { it.manager.release() }
        scope.cancel()
        _players.value = emptyMap()
    }

    private fun pickFreeSlot(protected: Set<String>): Slot =
        slots.firstOrNull { it.episodeId == null }
            ?: slots.firstOrNull { it.episodeId !in protected }
            ?: slots.first()

    private fun load(slot: Slot, request: Request) {
        val manager = slot.manager
        manager.setVolume(0f)

        manager.player.setMediaItems(
            mediaItems = listOf(request.url),
            startIndex = 0,
            startPositionMs = request.startPositionMs,
        )
        manager.pause()
        slot.episodeId = request.episodeId
    }

    private fun publish() {
        _players.value = slots
            .mapNotNull { slot -> slot.episodeId?.let { it to slot.manager } }
            .toMap()
    }

    private companion object {
        const val POOL_SIZE = 3
    }
}
