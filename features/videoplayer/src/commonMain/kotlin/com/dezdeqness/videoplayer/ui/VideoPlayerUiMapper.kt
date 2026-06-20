package com.dezdeqness.videoplayer.ui

import com.dezdeqness.release.contract.model.EpisodeEntity
import com.dezdeqness.release.contract.model.TimingEntity
import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import com.dezdeqness.videoplayer.ui.model.TimingUiItem

class VideoPlayerUiMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(item: EpisodeEntity) = EpisodeUiItem(
        id = item.id,
        name = item.name,
        previewUrl = imageUrlBuilder.build(item.previewUrl),
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
