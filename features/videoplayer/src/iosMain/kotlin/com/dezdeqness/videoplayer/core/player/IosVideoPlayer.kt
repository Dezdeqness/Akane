package com.dezdeqness.videoplayer.core.player

import platform.AVFoundation.AVPlayer
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.play
import platform.AVFoundation.pause
import platform.AVFoundation.seekToTime
import platform.AVFoundation.currentTime
import platform.AVFoundation.volume
import platform.AVFoundation.rate
import platform.AVFoundation.isMuted
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.AVPlayerItemDidPlayToEndTimeNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class IosVideoPlayer(
    private val avPlayer: AVPlayer
) : VideoPlayer {
    private var listeners = mutableListOf<VideoPlayerListener>()
    private var isPlayingState = false
    private var isLoadingState = false
    private var playbackState = 0 // 0: idle, 1: ready, 2: ended, 3: buffering

    init {
        // Observe playback state changes
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = avPlayer.currentItem,
            queue = null
        ) { _ ->
            listeners.forEach { it.onPlaybackStateChanged(2) } // Ended
        }
    }

    override fun play() {
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
        listeners.forEach { it.onPlaybackStateChanged(playbackState) }
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
        TODO("Not yet implemented")
    }

    override fun seekBack() {
        TODO("Not yet implemented")
    }

    override fun setVolume(volume: Float) {
        avPlayer.volume = volume
    }

    override fun getVolume(): Float = avPlayer.volume

    override fun setPlaybackSpeed(speed: Float) {
        avPlayer.rate = speed
    }

    override fun getCurrentPosition(): Long {
        return (CMTimeGetSeconds(avPlayer.currentTime()) * 1000).toLong()
    }

    override fun getDuration(): Long {
        val duration = avPlayer.currentItem?.duration ?: return 0L
        return (CMTimeGetSeconds(duration) * 1000).toLong()
    }

    override fun getTotalBufferedDuration(): Long {
        // Simplified; AVPlayer buffering is complex, approximate here
        return avPlayer.currentItem?.loadedTimeRanges?.firstOrNull()?.let {
            (CMTimeGetSeconds(it.CMTimeRangeValue.end) * 1000).toLong()
        } ?: 0L
    }

    override fun addListener(listener: VideoPlayerListener) {
        listeners.add(listener)
    }

    override fun setVideoItems(
        mediaItems: List<String>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        val url = mediaItems.getOrNull(startIndex) ?: return
        val playerItem = AVPlayerItem(uRL = NSURL.URLWithString(url)!!)
        avPlayer.replaceCurrentItemWithPlayerItem(playerItem)
        if (startPositionMs > 0) {
            avPlayer.seekToTime(CMTimeMake(startPositionMs, 1000))
        }
    }


    override val isPlaying: Boolean
        get() = isPlayingState
    override val isLoading: Boolean
        get() = isLoadingState
    override val playbackState: Int
        get() = playbackState
}