package com.dezdeqness.details.ui

import com.dezdeqness.details.domain.model.EpisodeEntity
import com.dezdeqness.details.ui.model.EpisodesUiModel

class EpisodesUiMapper {

    fun map(item: EpisodeEntity) =
        EpisodesUiModel(
            id = item.id,
            name = item.name,
            ordinal = item.ordinal,
            hls720 = item.hls720.orEmpty(),
        )
}
