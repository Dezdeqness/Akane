package com.dezdeqness.videoplayer.ui

import com.dezdeqness.release.contract.model.EpisodeEntity
import com.dezdeqness.release.contract.model.TimingEntity
import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import com.dezdeqness.videoplayer.ui.model.TimingUiItem

class VideoPlayerUiMapper {

    fun map(item: EpisodeEntity) = EpisodeUiItem(
        id = item.id,
        name = item.name,
        previewUrl = item.previewUrl.toAbsoluteImageUrl(),
        ordinal = item.ordinal,
        episodeUrls = item.episodeUrls,
        nameEnglish = item.nameEnglish,
        opening = mapTiming(item.opening),
        ending = mapTiming(item.ending),
    )

    private fun mapTiming(timing: TimingEntity?): TimingUiItem? {
        return if (timing == null) {
            null
        } else {
            TimingUiItem(
                start = timing.start,
                end = timing.end,
            )
        }
    }

}

private fun String.toAbsoluteImageUrl(): String {
    if (isBlank()) return ""
    return if (startsWith("http://") || startsWith("https://")) this else BaseUrl.BASE_URL_IMAGES + this
}
