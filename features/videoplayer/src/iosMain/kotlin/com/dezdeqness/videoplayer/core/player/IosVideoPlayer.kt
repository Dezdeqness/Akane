package com.dezdeqness.videoplayer.core.player

import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatusPaused
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.AVQueuePlayer
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.AVFoundation.volume
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeAdd
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.loadedTimeRanges
import platform.AVFoundation.removeTimeObserver
import platform.CoreMedia.CMTimeRange
import platform.Foundation.addObserver
import platform.Foundation.removeObserver
import platform.darwin.NSObjectProtocol

private const val SEEK_INCREMENT_MS = 10_000L

@OptIn(ExperimentalForeignApi::class)
class IosVideoPlayer : NSObject(), VideoPlayer {

    internal val avPlayer = AVQueuePlayer()

    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 64)
    override val events: Flow<PlayerEvent> = _events.asSharedFlow()

    private var timeObserver: Any? = null
    private var endObserver: NSObjectProtocol? = null

    init {
        observeTime()
        observeTimeControlStatus()
        observeEnded()
    }

    fun makePlayerView(): UIView {
        val vc = AVPlayerViewController()
        vc.player = avPlayer
        vc.showsPlaybackControls = false
        return vc.view
    }

    private fun observeTime() {
        val interval = CMTimeMake(value = 1, timescale = 2)
        timeObserver =
            avPlayer.addPeriodicTimeObserverForInterval(interval = interval, queue = null) { time ->
                val posMs = (CMTimeGetSeconds(time) * 1000.0).toLong().coerceAtLeast(0)
                val item = avPlayer.currentItem
                val durMs = item?.duration?.let { (CMTimeGetSeconds(it) * 1000.0).toLong() } ?: 0L
                _events.tryEmit(PlayerEvent.PositionChanged(posMs))
                _events.tryEmit(PlayerEvent.DurationChanged(durMs.coerceAtLeast(0)))

                val bufferedMs = item?.loadedTimeRanges?.firstObject?.let { obj ->
                    @Suppress("UNCHECKED_CAST")
                    val range = obj as CMTimeRange
                    val end = CMTimeAdd(range.start, range.duration)
                    (CMTimeGetSeconds(end) * 1000.0).toLong()
                } ?: 0L
                _events.tryEmit(PlayerEvent.BufferedChanged(bufferedMs.coerceAtLeast(0)))
            }
    }

    private fun observeTimeControlStatus() {
        addObserver(
            observer = this,
            forKeyPath = "avPlayer.timeControlStatus",
            options = NSKeyValueObservingOptionNew,
            context = null
        )
    }

    override fun observeValueForKeyPath(
        keyPath: String?,
        ofObject: Any?,
        change: Map<Any?, *>?,
        context: CPointer<*>?,
    ) {
        if (keyPath == "avPlayer.timeControlStatus") {
            when (avPlayer.timeControlStatus) {
                AVPlayerTimeControlStatusPlaying -> {
                    _events.tryEmit(PlayerEvent.IsBuffering(false))
                    _events.tryEmit(PlayerEvent.IsPlaying(true))
                }

                AVPlayerTimeControlStatusPaused -> {
                    _events.tryEmit(PlayerEvent.IsBuffering(false))
                    _events.tryEmit(PlayerEvent.IsPlaying(false))
                }

                AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate -> {
                    _events.tryEmit(PlayerEvent.IsBuffering(true))
                }
            }
        }
    }

    private fun observeEnded() {
        endObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = null,
            queue = null
        ) { _ ->
            _events.tryEmit(PlayerEvent.PlaybackEnded)
            _events.tryEmit(PlayerEvent.IsPlaying(false))
        }
    }

    override fun play() {
        avPlayer.play()
        _events.tryEmit(PlayerEvent.IsPlaying(true))
    }

    override fun pause() {
        avPlayer.pause()
        _events.tryEmit(PlayerEvent.IsPlaying(false))
    }

    override fun stop() {
        avPlayer.pause()
        avPlayer.seekToTime(CMTimeMake(0, 1))
        _events.tryEmit(PlayerEvent.IsPlaying(false))
    }

    override fun release() {
        avPlayer.pause()
        timeObserver?.let { avPlayer.removeTimeObserver(it) }
        timeObserver = null
        endObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        endObserver = null
        removeObserver(this, forKeyPath = "avPlayer.timeControlStatus")
    }

    override fun seekTo(positionMs: Long) {
        avPlayer.seekToTime(CMTimeMake(positionMs.coerceAtLeast(0), 1000))
    }

    override fun seekForward() {
        val currentMs = (CMTimeGetSeconds(avPlayer.currentTime()) * 1000.0).toLong()
        seekTo(currentMs + SEEK_INCREMENT_MS)
    }

    override fun seekBack() {
        val currentMs = (CMTimeGetSeconds(avPlayer.currentTime()) * 1000.0).toLong()
        seekTo((currentMs - SEEK_INCREMENT_MS).coerceAtLeast(0))
    }

    override fun setVolume(volume: Float) {
        avPlayer.volume = volume.coerceIn(0f, 1f)
    }

    override fun setPlaybackSpeed(speed: Float) {
        avPlayer.rate = speed.coerceIn(0.25f, 3f)
    }

    override fun setMediaItems(mediaItems: List<String>, startIndex: Int, startPositionMs: Long) {
        avPlayer.removeAllItems()
        mediaItems.drop(startIndex).forEach { url ->
            val nsUrl = NSURL.URLWithString(url) ?: return@forEach
            avPlayer.insertItem(AVPlayerItem(nsUrl), null)
        }
        if (startPositionMs > 0) seekTo(startPositionMs)
    }
}
