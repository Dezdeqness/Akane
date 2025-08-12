package com.dezdeqness.videoplayer.core.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AndroidVideoPlayer(
    private val exoPlayer: ExoPlayer
) : Player by exoPlayer, VideoPlayer {
    override fun addListener(listener: VideoPlayerListener) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                listener.onIsPlayingChanged(isPlaying)
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                listener.onIsLoadingChanged(isLoading)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                listener.isBuffering(playbackState == Player.STATE_BUFFERING)
            }
        })
    }

    override fun setVideoItems(
        mediaItems: List<String>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        exoPlayer.setMediaItems(mediaItems.map { MediaItem.fromUri(it) }, startIndex, startPositionMs)
        exoPlayer.prepare()
    }
}
