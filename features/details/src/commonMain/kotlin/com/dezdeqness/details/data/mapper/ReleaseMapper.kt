package com.dezdeqness.details.data.mapper

import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.network.models.response.ReleaseResponse

class ReleaseMapper(
    private val episodesManager: EpisodesManager,
) {

    fun map(response: ReleaseResponse) =
        ReleaseDetailsEntity(
            id = response.id,
            name = response.name.main,
            poster = BaseUrl.BASE_URL_IMAGES + response.poster.src,
            type = response.type.description.orEmpty(),
            description = response.description.orEmpty(),
            episodesTotal = response.episodesTotal ?: 0,
            genres = response.genres.map { it.name },
            episodes = response.episodes
                ?.map(episodesManager::map)
                ?.sortedByDescending { it.ordinal }
                .orEmpty(),
            year = response.year,
            isOngoing = response.isOngoing,
            ageRating = response.ageRating.label,
            userFavourites = response.userFavourites,
            averageDuration = response.averageDuration,
            planned = response.planned,
            watched = response.watched,
            watching = response.watching,
            postponed = response.postponed,
            abandoned = response.abandoned,
        )

}
