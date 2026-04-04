package com.dezdeqness.videoplayer.core.player.api

import com.dezdeqness.videoplayer.core.player.VideoPlayerUiState
import com.dezdeqness.videoplayer.core.player.data.MediaItem
import com.dezdeqness.videoplayer.core.player.data.MediaQuality
import kotlinx.coroutines.flow.StateFlow

interface PlayerContext {

    val playerState: StateFlow<VideoPlayerUiState>

    val controlsVisible: StateFlow<Boolean>

    val lockedControlsVisible: StateFlow<Boolean>

    val isLocked: StateFlow<Boolean>

    val autoHidePaused: StateFlow<Boolean>

    val playlist: StateFlow<List<MediaItem>>
    val currentItem: StateFlow<MediaItem?>
    val currentItemIndex: StateFlow<Int>

    val selectedQuality: StateFlow<MediaQuality>

    fun play()
    fun pause()
    fun stop()
    fun seekTo(ms: Long)
    fun seekForward()
    fun seekBack()
    fun setSpeed(speed: Float)
    fun setVolume(volume: Float)

    fun showControls()
    fun hideControls()
    fun setLocked(locked: Boolean)

    fun pauseAutoHide()

    fun resumeAutoHide()

    fun selectItem(id: String)
    fun selectItemByIndex(index: Int)

    fun setQuality(quality: MediaQuality)
}
