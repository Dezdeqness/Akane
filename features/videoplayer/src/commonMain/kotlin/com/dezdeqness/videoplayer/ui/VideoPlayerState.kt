package com.dezdeqness.videoplayer.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.release.contract.model.VideoQuality
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem

@Immutable
data class VideoPlayerState(
    val videoData: VideoData = VideoData(),
    val currentEpisodeId: String = "",
    val isPlaylistBottomSheetVisible: Boolean = false,
    val videoSpeedData: VideoSpeedData = VideoSpeedData(),
    val qualityData: QualityData = QualityData(),
)

@Immutable
data class VideoData(
    val title: String = "",
    val episodes: List<EpisodeUiItem> = listOf(),
    val status: Status = Status.Initial,
)

data class VideoSpeedData(
    val videoSpeed: VideoSpeed = VideoSpeed.Normal,
    val isVideoSpeedDropdownVisible: Boolean = false,
)

enum class Status {
    Loaded,
    Loading,
    Initial,
    Error
}

enum class VideoSpeed(val speed: Float, val speedName: String) {
    Slowest(0.5f, "0.5x"),
    Slower(0.75f, "0.75x"),
    Normal(1f, "1x"),
    Faster(1.25f, "1.25x"),
    Fast(1.5f, "1.5x"),
    VeryFast(1.75f, "1.75x"),
    Fastest(2f, "2x")
}

data class QualityData(
    val currentVideoQuality: VideoQuality = VideoQuality.q720,
    val isQualityDropdownVisible: Boolean = false,
)

enum class AspectRatioMode { Fit16_9, Fit4_3, Fill }
