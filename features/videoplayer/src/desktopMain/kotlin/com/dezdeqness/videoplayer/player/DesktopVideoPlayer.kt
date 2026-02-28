package com.dezdeqness.videoplayer.player

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.dezdeqness.videoplayer.core.player.PlayerEvent
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import kotlinx.coroutines.flow.*
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallbackAdapter
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.awt.image.BufferedImage
import java.nio.ByteBuffer

class DesktopVideoPlayer : VideoPlayer {

    private val factory = MediaPlayerFactory(listOf("--no-input-fast-seek"))
    internal val mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()

    private val _videoFrame = MutableStateFlow<ImageBitmap?>(null)
    val videoFrame: StateFlow<ImageBitmap?> = _videoFrame.asStateFlow()

    private var videoWidth = 0
    private var videoHeight = 0
    private var bufferedImage: BufferedImage? = null

    private val bufferFormatCallback = object : BufferFormatCallback {
        override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
            videoWidth = sourceWidth
            videoHeight = sourceHeight
            bufferedImage = BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_RGB)
            renderCallback.setBuffer(IntArray(sourceWidth * sourceHeight))
            return RV32BufferFormat(sourceWidth, sourceHeight)
        }

        override fun newFormatSize(
            bufferWidth: Int,
            bufferHeight: Int,
            displayWidth: Int,
            displayHeight: Int,
        ) {
            // no-op
        }

        override fun allocatedBuffers(buffers: Array<out ByteBuffer>) {
            // no-op
        }
    }

    private val renderCallback = object : RenderCallbackAdapter() {
        override fun onDisplay(mediaPlayer: MediaPlayer, rgbBuffer: IntArray) {
            val image = bufferedImage ?: return
            image.setRGB(0, 0, videoWidth, videoHeight, rgbBuffer, 0, videoWidth)
            _videoFrame.value = image.toComposeImageBitmap()
        }
    }

    init {
        mediaPlayer.videoSurface().set(
            CallbackVideoSurface(
                bufferFormatCallback,
                renderCallback,
                true,
                VideoSurfaceAdapters.getVideoSurfaceAdapter(),
            )
        )

        mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer?) {
                _events.tryEmit(PlayerEvent.IsPlaying(true))
                _events.tryEmit(PlayerEvent.IsBuffering(false))
            }

            override fun paused(mediaPlayer: MediaPlayer?) {
                _events.tryEmit(PlayerEvent.IsPlaying(false))
            }

            override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
                _events.tryEmit(
                    PlayerEvent.IsBuffering(newCache < 100f)
                )
            }

            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                _events.tryEmit(
                    PlayerEvent.PositionChanged(newTime)
                )
            }

            override fun lengthChanged(mediaPlayer: MediaPlayer?, newLength: Long) {
                _events.tryEmit(
                    PlayerEvent.DurationChanged(newLength)
                )
            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                _events.tryEmit(PlayerEvent.PlaybackEnded)
                _events.tryEmit(PlayerEvent.IsPlaying(false))
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                _events.tryEmit(PlayerEvent.Error("VLC playback error"))
            }
        })
    }

    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 64)
    override val events: Flow<PlayerEvent> = _events.asSharedFlow()

    override fun play() {
        mediaPlayer.controls().play()
    }

    override fun pause() {
        mediaPlayer.controls().pause()
    }

    override fun stop() {
        mediaPlayer.controls().stop()
        _events.tryEmit(PlayerEvent.IsPlaying(false))
    }

    override fun release() {
        mediaPlayer.release()
        factory.release()
    }

    override fun seekTo(positionMs: Long) {
        mediaPlayer.controls().setTime(positionMs.coerceAtLeast(0))
    }

    override fun setVolume(volume: Float) {
        mediaPlayer.audio().setVolume((volume.coerceIn(0f, 1f) * 100).toInt())
    }

    override fun setPlaybackSpeed(speed: Float) {
        mediaPlayer.controls().setRate(speed.coerceIn(0.25f, 3f))
    }

    override fun seekBack() {
        val currentTime = mediaPlayer.status().time()
        seekTo((currentTime - 10_000).coerceAtLeast(0))
    }

    override fun seekForward() {
        val currentTime = mediaPlayer.status().time()
        val duration = mediaPlayer.status().length()
        seekTo((currentTime + 10_000).coerceAtMost(duration))
    }

    override fun setMediaItems(mediaItems: List<String>, startIndex: Int, startPositionMs: Long) {
        val url = mediaItems.getOrNull(startIndex) ?: return
        mediaPlayer.media().play(url)
        if (startPositionMs > 0) seekTo(startPositionMs)
    }
}
