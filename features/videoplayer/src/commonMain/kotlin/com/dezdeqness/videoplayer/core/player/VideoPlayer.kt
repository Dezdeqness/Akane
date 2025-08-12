package com.dezdeqness.videoplayer.core.player

interface VideoPlayer {
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(positionMs: Long)
    fun seekForward()
    fun seekBack()
    fun setVolume(volume: Float)
    fun getVolume(): Float
    fun setPlaybackSpeed(speed: Float)
    fun getCurrentPosition(): Long
    fun getDuration(): Long
    fun getTotalBufferedDuration(): Long
    fun addListener(listener: VideoPlayerListener)
    fun setVideoItems(mediaItems: List<String>, startIndex: Int, startPositionMs: Long)
}
