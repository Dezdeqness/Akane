package com.dezdeqness.videoplayer.player.mpv

import androidx.compose.ui.graphics.ImageBitmap
import com.dezdeqness.videoplayer.core.player.PlayerEvent
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import com.dezdeqness.videoplayer.player.FrameProvidingPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class MpvVideoPlayer : VideoPlayer, FrameProvidingPlayer {

    private val mpv = Mpv()

    override val events: Flow<PlayerEvent> = mpv.events
    override val videoFrame: StateFlow<ImageBitmap?> = mpv.videoFrame

    override fun play() = mpv.play()
    override fun pause() = mpv.pause()
    override fun stop() = mpv.stop()
    override fun release() = mpv.release()

    override fun seekTo(positionMs: Long) = mpv.seekTo(positionMs)
    override fun seekBack() = mpv.seekBack()
    override fun seekForward() = mpv.seekForward()
    override fun setVolume(volume: Float) = mpv.setVolume(volume)
    override fun setPlaybackSpeed(speed: Float) = mpv.setSpeed(speed)

    override fun setMediaItems(mediaItems: List<String>, startIndex: Int, startPositionMs: Long) {
        val url = mediaItems.getOrNull(startIndex) ?: return
        mpv.load(url, startPositionMs)
    }
}
