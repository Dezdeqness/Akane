package com.dezdeqness.videoplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dezdeqness.screenshot.DefaultShots
import com.dezdeqness.screenshot.IMAGE_LOAD_ADVANCE_MS
import com.dezdeqness.screenshot.ThemeShot
import com.dezdeqness.screenshot.screenshotViewports
import com.dezdeqness.videoplayer.EpisodeEndOverlay
import com.dezdeqness.videoplayer.core.player.EpisodeEndOverlayUiState
import com.dezdeqness.videoplayer.core.player.PlayerEvent
import com.dezdeqness.videoplayer.core.player.VideoPlayerLayout
import com.dezdeqness.videoplayer.core.player.VideoPlayerManager
import com.dezdeqness.videoplayer.core.player.data.MediaItem
import com.dezdeqness.videoplayer.core.player.data.MediaQuality
import com.dezdeqness.videoplayer.core.player.data.MediaSource
import com.dezdeqness.videoplayer.core.player.data.QualityVariant
import com.dezdeqness.videoplayer.core.player.data.SkipRange
import com.dezdeqness.videoplayer.core.player.feature.installPlatformFeatures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test

class VideoPlayerScreenshotTest {

    private val shots: List<ThemeShot> = DefaultShots.filter { it.themeLabel == "dark" }

    private val DURATION = 1_440_000L

    @Test
    fun playing() = screenshotViewports("player_playing", shots = shots) { _ ->
        PlayerLayout(
            PlayerEvent.IsPlaying(true),
            PlayerEvent.DurationChanged(DURATION),
            PlayerEvent.PositionChanged(432_000L),
            PlayerEvent.BufferedChanged(600_000L),
        )
    }

    @Test
    fun paused() = screenshotViewports("player_paused", shots = shots) { _ ->
        PlayerLayout(
            PlayerEvent.IsPlaying(false),
            PlayerEvent.DurationChanged(DURATION),
            PlayerEvent.PositionChanged(432_000L),
            PlayerEvent.BufferedChanged(600_000L),
        )
    }

    @Test
    fun buffering() = screenshotViewports("player_buffering", shots = shots) { _ ->
        PlayerLayout(
            PlayerEvent.IsPlaying(true),
            PlayerEvent.IsBuffering(true),
            PlayerEvent.DurationChanged(DURATION),
            PlayerEvent.PositionChanged(120_000L),
        )
    }

    @Test
    fun locked() = screenshotViewports("player_locked", shots = shots) { _ ->
        PlayerLayout(
            PlayerEvent.IsPlaying(true),
            PlayerEvent.DurationChanged(DURATION),
            PlayerEvent.PositionChanged(432_000L),
            configure = {
                setLocked(true)
                showControls()
            },
        )
    }

    @Test
    fun skipOpening() = screenshotViewports("player_skip_opening", shots = shots) { _ ->
        PlayerLayout(
            PlayerEvent.IsPlaying(true),
            PlayerEvent.DurationChanged(DURATION),
            PlayerEvent.PositionChanged(30_000L),
            PlayerEvent.BufferedChanged(120_000L),
        )
    }

    @Test
    fun episodeEndAutoNext() = screenshotViewports(
        name = "player_episode_end_auto_next",
        advanceMs = IMAGE_LOAD_ADVANCE_MS,
        shots = shots,
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            EpisodeEndOverlay(
                state = EpisodeEndOverlayUiState.AutoNext(nextIndex = 1, previewUrl = "preview"),
                onAutoNext = {},
                onRetry = {},
                onBack = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    @Test
    fun episodeEndRetry() = screenshotViewports(
        name = "player_episode_end_retry",
        advanceMs = IMAGE_LOAD_ADVANCE_MS,
        shots = shots,
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            EpisodeEndOverlay(
                state = EpisodeEndOverlayUiState.Retry(previewUrl = "preview"),
                onAutoNext = {},
                onRetry = {},
                onBack = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    @Composable
    private fun PlayerLayout(
        vararg events: PlayerEvent,
        configure: VideoPlayerManager.() -> Unit = {},
    ) {
        val manager = remember {
            val fake = FakeVideoPlayer()
            VideoPlayerManager(player = fake, scope = CoroutineScope(Dispatchers.Unconfined)).apply {
                installPlatformFeatures()
                setPlaylist(items = fakePlaylist(), startIndex = 0)
                fake.emit(*events)
                configure()
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            VideoPlayerLayout(engine = manager, modifier = Modifier.fillMaxSize())
        }
    }

    private fun fakePlaylist(): List<MediaItem> = List(3) { index ->
        MediaItem(
            id = "ep-${index + 1}",
            name = "Начало пути",
            ordinal = (index + 1).toLong(),
            source = MediaSource.MultiQuality(
                variants = listOf(
                    QualityVariant(MediaQuality.q480, "https://example.com/480"),
                    QualityVariant(MediaQuality.q720, "https://example.com/720"),
                    QualityVariant(MediaQuality.q1080, "https://example.com/1080"),
                ),
            ),
            opening = SkipRange(startMs = 5_000L, endMs = 95_000L),
            ending = SkipRange(startMs = 1_380_000L, endMs = DURATION),
        )
    }
}
