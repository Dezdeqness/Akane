package com.dezdeqness.details.data.mapper

import com.dezdeqness.release.contract.model.ReleaseDetailsEntity
import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.response.ReleaseResponse

class ReleaseMapper(
    private val episodesManager: EpisodesManager,
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: ReleaseResponse) =
        ReleaseDetailsEntity(
            id = response.id,
            name = response.name.main,
            poster = imageUrlBuilder.build(response.poster.src),
            type = response.type.description.orEmpty(),
            description = response.description.orEmpty(),
            episodesTotal = response.episodesTotal ?: 0,
            genres = response.genres?.map { it.name } ?: emptyList(),
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
            season = response.season.description.orEmpty(),
        )

}
