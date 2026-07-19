package com.dezdeqness.videoplayer.core.player

import co.touchlab.kermit.Logger
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemFailedToPlayToEndTimeNotification
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
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.UIKit.UIView
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.loadedTimeRanges
import platform.AVFoundation.removeTimeObserver
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.sizeOf
import platform.CoreMedia.CMTimeRange
import platform.Foundation.NSValue
import platform.Foundation.NSFileManager
import platform.Foundation.NSData
import platform.Foundation.create

private const val SEEK_INCREMENT_MS = 10_000L

@OptIn(ExperimentalForeignApi::class)
class IosVideoPlayer : VideoPlayer {

    internal val avPlayer = AVQueuePlayer()
    private var playerViewController: AVPlayerViewController? = null

    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 64)
    override val events: Flow<PlayerEvent> = _events.asSharedFlow()

    private var timeObserver: Any? = null
    private var endObserver: platform.darwin.NSObjectProtocol? = null
    private var failedObserver: platform.darwin.NSObjectProtocol? = null

    private var lastTimeControlStatus: Long = -1
    private var localFileServer: LocalFileServer? = null

    init {
        observeTime()
        observeEnded()
        observeFailed()
    }

    fun makePlayerView(): UIView {
        val vc = AVPlayerViewController()
        vc.player = avPlayer
        vc.showsPlaybackControls = false
        playerViewController = vc
        return vc.view
    }

    private fun observeTime() {
        val interval = CMTimeMake(value = 1L, timescale = 2)
        timeObserver =
            avPlayer.addPeriodicTimeObserverForInterval(interval = interval, queue = null) { time ->
                val posMs = (CMTimeGetSeconds(time) * 1000.0).toLong().coerceAtLeast(0)
                val item = avPlayer.currentItem
                val durMs = item?.duration?.let { (CMTimeGetSeconds(it) * 1000.0).toLong() } ?: 0L
                _events.tryEmit(PlayerEvent.PositionChanged(posMs))
                _events.tryEmit(PlayerEvent.DurationChanged(durMs.coerceAtLeast(0)))

                val bufferedMs = item?.loadedTimeRanges?.firstOrNull()?.let { obj ->
                    memScoped {
                        val range = alloc<CMTimeRange>()
                        (obj as NSValue).getValue(range.ptr, sizeOf<CMTimeRange>().toULong())
                        val startSec = range.start.value.toDouble() / range.start.timescale.toDouble()
                        val durSec = range.duration.value.toDouble() / range.duration.timescale.toDouble()
                        ((startSec + durSec) * 1000.0).toLong()
                    }
                } ?: 0L
                _events.tryEmit(PlayerEvent.BufferedChanged(bufferedMs.coerceAtLeast(0)))

                val status = avPlayer.timeControlStatus
                if (status != lastTimeControlStatus) {
                    lastTimeControlStatus = status
                    when (status) {
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

    private fun observeFailed() {
        failedObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemFailedToPlayToEndTimeNotification,
            `object` = null,
            queue = null
        ) { notification ->
            val item = notification?.`object` as? AVPlayerItem
            Logger.e(TAG) { "AVPlayerItem failed to play: ${item?.error?.localizedDescription}" }
            Logger.e(TAG) { "Error code: ${item?.error?.code}, domain: ${item?.error?.domain}" }
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
        avPlayer.seekToTime(CMTimeMake(value = 0L, timescale = 1))
        _events.tryEmit(PlayerEvent.IsPlaying(false))
    }

    override fun release() {
        avPlayer.pause()
        timeObserver?.let { avPlayer.removeTimeObserver(it) }
        timeObserver = null
        endObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        endObserver = null
        failedObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        failedObserver = null
        localFileServer?.stop()
        localFileServer = null
        playerViewController = null
    }

    override fun seekTo(positionMs: Long) {
        avPlayer.seekToTime(CMTimeMake(positionMs.coerceAtLeast(0), 1000))
        _events.tryEmit(PlayerEvent.PositionChanged(positionMs))
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

        localFileServer?.stop()
        localFileServer = null

        mediaItems.drop(startIndex).forEach { url ->
            val isLocalFile = url.startsWith("/")

            if (url.startsWith(BOOKMARK_PREFIX)) {
                val nsUrl = resolveBookmarkUrl(url)
                if (nsUrl == null) {
                    Logger.e(TAG) { "Failed to resolve bookmark for downloaded asset" }
                    return@forEach
                }
                Logger.d(TAG) { "Adding downloaded asset: ${nsUrl.path}" }
                avPlayer.insertItem(AVPlayerItem(nsUrl), null)
            } else if (isLocalFile && url.endsWith(".m3u8")) {
                val dir = url.substringBeforeLast("/")
                val playlistName = url.substringAfterLast("/")
                val server = LocalFileServer()
                val baseUrl = server.start(dir)
                if (baseUrl.isEmpty()) {
                    Logger.e(TAG) { "Failed to start local file server for $dir" }
                    return@forEach
                }
                localFileServer = server
                val httpUrl = "$baseUrl/$playlistName"
                Logger.d(TAG) { "Serving local HLS via HTTP: $httpUrl" }
                val nsUrl = NSURL.URLWithString(httpUrl) ?: run {
                    Logger.e(TAG) { "Invalid HTTP URL: $httpUrl" }
                    return@forEach
                }
                avPlayer.insertItem(AVPlayerItem(nsUrl), null)
            } else if (isLocalFile) {
                if (!NSFileManager.defaultManager.fileExistsAtPath(url)) {
                    Logger.e(TAG) { "File does not exist: $url" }
                    return@forEach
                }
                val nsUrl = NSURL.fileURLWithPath(url)
                Logger.d(TAG) { "Adding local file: $nsUrl" }
                avPlayer.insertItem(AVPlayerItem(nsUrl), null)
            } else {
                val nsUrl = NSURL.URLWithString(url) ?: run {
                    Logger.e(TAG) { "Invalid URL: $url" }
                    return@forEach
                }
                Logger.d(TAG) { "Adding media item: $nsUrl" }
                avPlayer.insertItem(AVPlayerItem(nsUrl), null)
            }
        }
        if (startPositionMs > 0) seekTo(startPositionMs)
    }

    private fun resolveBookmarkUrl(url: String): NSURL? {
        val base64 = url.removePrefix(BOOKMARK_PREFIX)
        val data = NSData.create(base64EncodedString = base64, options = 0u) ?: return null

        val resolved = NSURL.URLByResolvingBookmarkData(
            bookmarkData = data,
            options = 0u,
            relativeToURL = null,
            bookmarkDataIsStale = null,
            error = null,
        )

        val path = resolved?.path ?: return null
        return if (NSFileManager.defaultManager.fileExistsAtPath(path)) resolved else null
    }

    companion object {
        private const val TAG = "IosVideoPlayer"
        private const val BOOKMARK_PREFIX = "bookmark://"
    }
}
