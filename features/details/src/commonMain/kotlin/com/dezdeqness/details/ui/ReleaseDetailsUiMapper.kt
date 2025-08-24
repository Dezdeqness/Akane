package com.dezdeqness.details.ui

import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel
import com.dezdeqness.network.constants.BaseUrl

class ReleaseDetailsUiMapper(
    private val episodesUiMapper: EpisodesUiMapper,
) {

    fun map(item: ReleaseDetailsEntity) = ReleaseDetailsUiModel(
        id = item.id,
        title = item.name,
        summary = item.description,
        imageUrl = item.poster,
        genres = item.genres,
        episodesTotal = item.episodesTotal,
        episodes = item.episodes.map(episodesUiMapper::map)
    )

}
