package com.dezdeqness.videoplayer.core.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.dezdeqness.videoplayer.core.player.api.VideoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@UnstableApi
class AndroidVideoPlayer(
    context: Context,
    downloadCache: Cache? = null,
) : VideoPlayer {

    val exoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(createDataSourceFactory(context, downloadCache)))
        .setSeekForwardIncrementMs(10000L)
        .setSeekBackIncrementMs(10000L)
        .build()


    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 64)
    override val events: Flow<PlayerEvent> = _events.asSharedFlow()

    private var tickerJob: Job? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _events.tryEmit(PlayerEvent.IsPlaying(isPlaying))
                if (isPlaying) startTicker() else stopTicker()
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> _events.tryEmit(PlayerEvent.IsBuffering(true))
                    Player.STATE_READY -> _events.tryEmit(PlayerEvent.IsBuffering(false))
                    Player.STATE_ENDED -> _events.tryEmit(PlayerEvent.PlaybackEnded)
                }
                _events.tryEmit(PlayerEvent.DurationChanged(exoPlayer.duration.coerceAtLeast(0)))
            }

            override fun onPlayerError(error: PlaybackException) {
                _events.tryEmit(PlayerEvent.Error(error.message ?: "Playback error"))
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                _events.tryEmit(PlayerEvent.DurationChanged(exoPlayer.duration.coerceAtLeast(0)))
            }
        })
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    _events.emit(PlayerEvent.PositionChanged(exoPlayer.currentPosition))
                    _events.emit(PlayerEvent.BufferedChanged(exoPlayer.bufferedPosition))
                }
                delay(500)
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel(); tickerJob = null
    }

    override fun play() = exoPlayer.play()
    override fun pause() = exoPlayer.pause()
    override fun stop() {
        exoPlayer.stop()
        stopTicker()
        _events.tryEmit(PlayerEvent.IsPlaying(false))
    }

    override fun release() {
        stopTicker()
        exoPlayer.stop()
        exoPlayer.release()
        scope.cancel()
    }

    override fun seekBack() = exoPlayer.seekBack().also {
        _events.tryEmit(PlayerEvent.PositionChanged(exoPlayer.currentPosition))
    }
    override fun seekForward() = exoPlayer.seekForward().also {
        _events.tryEmit(PlayerEvent.PositionChanged(exoPlayer.currentPosition))
    }
    override fun seekTo(positionMs: Long) = exoPlayer.seekTo(positionMs.coerceAtLeast(0)).also {
        _events.tryEmit(PlayerEvent.PositionChanged(exoPlayer.currentPosition))
    }
    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume.coerceIn(0f, 1f)
    }

    override fun setPlaybackSpeed(speed: Float) {
        exoPlayer.setPlaybackSpeed(speed.coerceIn(0.25f, 3f))
    }

    override fun setMediaItems(mediaItems: List<String>, startIndex: Int, startPositionMs: Long) {
        val items = mediaItems.map { uri ->
            val builder = MediaItem.Builder().setUri(uri)
            builder.build()
        }
        exoPlayer.setMediaItems(items, startIndex, startPositionMs)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    companion object {
        private fun createDataSourceFactory(context: Context, cache: Cache?): DataSource.Factory {
            val upstream = DefaultDataSource.Factory(context)
            if (cache == null) return upstream

            return CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(upstream)
                .setCacheWriteDataSinkFactory(null)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }
    }
}
