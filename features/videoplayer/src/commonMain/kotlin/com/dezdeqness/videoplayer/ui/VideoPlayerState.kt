package com.dezdeqness.videoplayer.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem

@Immutable
data class VideoPlayerState(
    val title: String = "",
    val episodes: List<EpisodeUiItem> = listOf(),
    val status: Status = Status.Initial,
    val currentEpisodeId: String = "",
)

enum class Status {
    Loaded,
    Loading,
    Initial,
    Error
}
