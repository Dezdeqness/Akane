package com.dezdeqness.videoplayer.ui.model

import androidx.compose.runtime.Stable
import com.dezdeqness.release.contract.model.VideoQuality

@Stable
data class EpisodeUiItem(
    val id: String,
    val name: String,
    val previewUrl: String,
    val ordinal: Long,
    val episodeUrls: LinkedHashMap<VideoQuality, String>,
    val nameEnglish: String?,
    val opening: TimingUiItem?,
    val ending: TimingUiItem?,
)

data class TimingUiItem(
    val start: Long,
    val end: Long,
) {
    fun markers() = buildList {
        add(start)
        add(end)
    }
}
