package com.dezdeqness.videoplayer.ui

import com.dezdeqness.details.domain.model.EpisodeEntity
import com.dezdeqness.details.domain.model.TimingEntity
import com.dezdeqness.network.models.core.Timing
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem
import com.dezdeqness.videoplayer.ui.model.TimingUiItem

class VideoPlayerUiMapper {

    fun map(item: EpisodeEntity) = EpisodeUiItem(
        id = item.id,
        name = item.name,
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
