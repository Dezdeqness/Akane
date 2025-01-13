package com.dezdeqness.details.data.mapper

import com.dezdeqness.details.domain.model.EpisodeEntity
import com.dezdeqness.network.models.response.Episode

class EpisodesManager {

    fun map(response: Episode) =
        EpisodeEntity(
            id = response.id,
            name = response.name,
            ordinal = response.ordinal,
            hls480 = response.hls480,
            hls720 = response.hls720,
            hls1080 = response.hls1080,
            duration = response.duration,
            updatedAt = response.updatedAt,
            nameEnglish = response.nameEnglish.orEmpty(),
        )

}
