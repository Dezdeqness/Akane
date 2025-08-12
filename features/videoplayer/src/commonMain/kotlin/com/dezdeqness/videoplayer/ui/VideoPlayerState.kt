package com.dezdeqness.videoplayer.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem

@Immutable
data class VideoPlayerState(
    val videoData: VideoData = VideoData(),
    val currentEpisodeId: String = "",
    val isPlaylistBottomSheetVisible: Boolean = false,
    val videoSpeedData: VideoSpeedData = VideoSpeedData(),
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

enum class VideoSpeed(val speed: Float) {
    Slowest(0.5f),
    Slower(0.75f),
    Normal(1f),
    Faster(1.25f),
    Fast(1.5f),
    VeryFast(1.75f),
    Fastest(2f)
}
