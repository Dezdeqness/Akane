package com.dezdeqness.details.ui

import com.dezdeqness.details.domain.model.EpisodeEntity
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.network.constants.BaseUrl

class EpisodesUiMapper {

    fun map(item: EpisodeEntity) =
        EpisodesUiModel(
            id = item.id,
            name = item.name,
            previewUrl = if (item.previewUrl.isNotEmpty()) BaseUrl.BASE_URL_IMAGES + item.previewUrl else "",
            ordinal = item.ordinal,
            hls720 = item.hls720.orEmpty(),
        )
}
