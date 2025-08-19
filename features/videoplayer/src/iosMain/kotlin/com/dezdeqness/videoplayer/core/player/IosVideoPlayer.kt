package com.dezdeqness.videoplayer.core.player

import co.touchlab.kermit.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVQueuePlayer
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSURL
import platform.AVFoundation.volume
import platform.Foundation.NSNotificationCenter
import platform.CoreMedia.CMTimeAdd
import platform.CoreMedia.CMTimeSubtract

@OptIn(ExperimentalForeignApi::class)
class IosVideoPlayer(
    val avPlayer: AVQueuePlayer
) : VideoPlayer {
    private var isPlayingState = false
    private var isLoadingState = false
    private var playbackState = 0 // 0: idle, 1: ready, 2: ended, 3: buffering

    private var mediaItems: List<String> = listOf()
    private var speedBackRate = 1f

    private var listeners: ArrayList<VideoPlayerListener> = ArrayList()

    override fun play() {
        avPlayer.rate = speedBackRate
        avPlayer.play()
        isPlayingState = true
        listeners.forEach { it.onIsPlayingChanged(true) }
    }

    override fun pause() {
        avPlayer.pause()
        isPlayingState = false
        listeners.forEach { it.onIsPlayingChanged(false) }
    }

    override fun stop() {
        avPlayer.pause()
        avPlayer.seekToTime(CMTimeMake(0, 1))
        isPlayingState = false
        playbackState = 0
        listeners.forEach { it.onIsPlayingChanged(false) }
        listeners.forEach { it.onIsLoadingChanged(false) }
    }

    override fun release() {
        avPlayer.pause()
        NSNotificationCenter.defaultCenter.removeObserver(avPlayer)
    }

    override fun seekTo(positionMs: Long) {
        val time = CMTimeMake(positionMs, 1000)
        avPlayer.seekToTime(time)
    }

    override fun seekForward() {
        val currentTime = avPlayer.currentTime()
        val newTime = CMTimeAdd(currentTime, CMTimeMake(10000, 1000))
        avPlayer.seekToTime(newTime)
    }

    override fun seekBack() {
        val currentTime = avPlayer.currentTime()
        val newTime = CMTimeSubtract(currentTime, CMTimeMake(10000, 1000))
        avPlayer.seekToTime(newTime)
    }

    override fun setVolume(volume: Float) {
        avPlayer.volume = volume
    }

    override fun getVolume(): Float = avPlayer.volume

    override fun setPlaybackSpeed(speed: Float) {
        speedBackRate = speed
        avPlayer.rate = speed
    }

    override fun getCurrentPosition(): Long {
        return (CMTimeGetSeconds(avPlayer.currentTime()) * 1000).toLong()
    }

    override fun getDuration(): Long {
        val duration = avPlayer.currentItem()?.duration ?: return 0L
        return (CMTimeGetSeconds(duration) * 1000).toLong()
    }

    override fun getTotalBufferedDuration(): Long {
        // no op
        return 0
    }

    override fun addListener(listener: VideoPlayerListener) {
        listeners.add(listener)
        listener.isBuffering(false)
        listener.onIsPlayingChanged(false)
    }

    override fun setVideoItems(
        mediaItems: List<String>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        this.mediaItems = mediaItems

        val items = mediaItems.map { AVPlayerItem(uRL = NSURL.URLWithString(it)!!) }
        playByIndex(startIndex, items)

        if (startPositionMs > 0) {
            avPlayer.seekToTime(CMTimeMake(startPositionMs, 1000))
        }
    }

    private fun playByIndex(index: Int, list: List<AVPlayerItem>) {
        avPlayer.removeAllItems()
        val items = list.drop(index)
        items.forEach { item ->
            if (avPlayer.canInsertItem(item, null)) {
                avPlayer.insertItem(item, null)
            }
        }
    }
}