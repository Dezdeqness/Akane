package com.dezdeqness.videoplayer.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem

@Immutable
data class VideoPlayerState(
    val episodes: List<EpisodeUiItem> = listOf(),
)

