package com.dezdeqness.videoplayer.core.player

import androidx.compose.runtime.Stable
import com.dezdeqness.videoplayer.core.player.api.PlayerContext
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import com.dezdeqness.videoplayer.core.player.data.MediaItem
import com.dezdeqness.videoplayer.core.player.data.MediaQuality
import com.dezdeqness.videoplayer.core.player.data.resolveUrl
import com.dezdeqness.videoplayer.core.player.feature.FeatureRegistry
import com.dezdeqness.videoplayer.core.player.feature.PlayerFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
class VideoPlayerManager(
    val player: VideoPlayer,
    private val scope: CoroutineScope,
) : PlayerContext {

    private val _playerState = MutableStateFlow(VideoPlayerUiState())
    override val playerState: StateFlow<VideoPlayerUiState> = _playerState

    private val _controlsVisible = MutableStateFlow(true)
    override val controlsVisible: StateFlow<Boolean> = _controlsVisible

    private val _isLocked = MutableStateFlow(false)
    override val isLocked: StateFlow<Boolean> = _isLocked

    private val _autoHidePauseCount = MutableStateFlow(0)
    override val autoHidePaused: StateFlow<Boolean> =
        _autoHidePauseCount.map { it > 0 }.stateIn(scope, SharingStarted.Eagerly, false)

    private val _playlist = MutableStateFlow<List<MediaItem>>(emptyList())
    override val playlist: StateFlow<List<MediaItem>> = _playlist

    private val _currentIndex = MutableStateFlow(0)
    override val currentItemIndex: StateFlow<Int> = _currentIndex

    override val currentItem: StateFlow<MediaItem?> = combine(_playlist, _currentIndex) { list, index ->
        list.getOrNull(index)
    }.stateIn(scope, SharingStarted.Eagerly, null)

    private val _selectedQuality = MutableStateFlow(MediaQuality.q720)
    override val selectedQuality: StateFlow<MediaQuality> = _selectedQuality

    private val _registry = FeatureRegistry()
    val registry get() = _registry

    init {
        scope.launch { player.events.collect { reduce(it) } }

        scope.launch {
            var previousKey: PlaylistKey? = null

            combine(_playlist, _currentIndex, _selectedQuality, ::PlaylistKey)
                .distinctUntilChanged()
                .collect { key ->
                    if (key.playlist.isEmpty()) return@collect

                    val urls = key.playlist.map { it.source.resolveUrl(key.quality) }

                    val previous = previousKey
                    val qualityOnlySwitch = previous?.let {
                        it.playlist == key.playlist &&
                                it.index == key.index &&
                                it.quality != key.quality
                    } == true

                    val startPositionMs = if (qualityOnlySwitch) _playerState.value.position else 0L
                    previousKey = key

                    player.setMediaItems(
                        mediaItems = urls,
                        startIndex = key.index.coerceIn(0, urls.lastIndex),
                        startPositionMs = startPositionMs,
                    )
                }
        }
    }

    fun installFeature(feature: PlayerFeature) {
        _registry.register(feature)
        feature.install(context = this, scope = scope)
    }

    fun installFeatures(vararg features: PlayerFeature) = features.forEach { installFeature(it) }


    fun setPlaylist(
        items: List<MediaItem>,
        startIndex: Int = 0,
        startPositionMs: Long = 0L,
    ) {
        _currentIndex.value = startIndex.coerceAtLeast(0)
        _playlist.value = items
    }

    override fun selectItem(id: String) {
        val index = _playlist.value.indexOfFirst { it.id == id }
        if (index >= 0) _currentIndex.value = index
    }

    override fun selectItemByIndex(index: Int) {
        _currentIndex.value = index.coerceIn(0, (_playlist.value.size - 1).coerceAtLeast(0))
    }

    override fun setQuality(quality: MediaQuality) {
        _selectedQuality.value = quality
    }

    override fun play() = player.play()
    override fun pause() = player.pause()
    override fun stop() = player.stop()
    override fun seekTo(ms: Long) = player.seekTo(ms)
    override fun seekForward() = player.seekForward()
    override fun seekBack() = player.seekBack()
    override fun setSpeed(speed: Float) = player.setPlaybackSpeed(speed.coerceIn(0.25f, 3f))
    override fun setVolume(volume: Float) = player.setVolume(volume.coerceIn(0f, 1f))

    override fun showControls() {
        _controlsVisible.value = true
    }

    override fun hideControls() {
        _controlsVisible.value = false
    }

    override fun setLocked(locked: Boolean) {
        _isLocked.value = locked
        if (locked) _controlsVisible.value = false
    }

    override fun pauseAutoHide() {
        _autoHidePauseCount.update { it + 1 }
    }

    override fun resumeAutoHide() {
        _autoHidePauseCount.update { (it - 1).coerceAtLeast(0) }
    }

    fun release() {
        _registry.allFeatures().forEach { it.dispose() }
        player.release()
    }

    private fun reduce(event: PlayerEvent) {
        _playerState.update { prev ->
            when (event) {
                is PlayerEvent.IsPlaying -> prev.copy(isPlaying = event.value)
                is PlayerEvent.IsBuffering -> prev.copy(isBuffering = event.value)
                is PlayerEvent.DurationChanged -> prev.copy(duration = event.durationMs.coerceAtLeast(0))
                is PlayerEvent.PositionChanged -> prev.copy(position = event.positionMs.coerceAtLeast(0))
                is PlayerEvent.BufferedChanged -> prev.copy(buffered = event.bufferedMs.coerceAtLeast(0))
                is PlayerEvent.PlaybackEnded -> prev.copy(isPlaying = false)
                is PlayerEvent.Error -> prev.copy(error = event.message)
            }
        }
    }

    private data class PlaylistKey(
        val playlist: List<MediaItem>,
        val index: Int,
        val quality: MediaQuality,
    )
}
