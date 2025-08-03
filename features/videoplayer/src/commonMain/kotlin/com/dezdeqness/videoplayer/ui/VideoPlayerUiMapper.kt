package com.dezdeqness.videoplayer.ui

import com.dezdeqness.details.domain.model.EpisodeEntity
import com.dezdeqness.videoplayer.ui.model.EpisodeUiItem

class VideoPlayerUiMapper {

    fun map(item: EpisodeEntity) = EpisodeUiItem(
        id = item.id,
        name = item.name,
        ordinal = item.ordinal,
        hls480 = item.hls480,
        hls720 = item.hls720,
        hls1080 = item.hls1080,
        nameEnglish = item.nameEnglish,
    )
}
