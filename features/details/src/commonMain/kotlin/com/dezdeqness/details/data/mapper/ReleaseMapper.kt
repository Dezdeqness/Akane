package com.dezdeqness.details.data.mapper

import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.network.models.response.ReleaseResponse

class ReleaseMapper(
    private val episodesManager: EpisodesManager,
) {

    fun map(response: ReleaseResponse) =
        ReleaseDetailsEntity(
            id = response.id,
            name = response.name.main,
            poster = response.poster.src,
            type = response.type.value,
            description = response.description.orEmpty(),
            episodesTotal = response.episodesTotal,
            genres = response.genres.map { it.name },
            episodes = response.episodes.map(episodesManager::map),
        )

}
