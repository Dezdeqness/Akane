package com.dezdeqness.details.ui

import com.dezdeqness.details.domain.model.FranchiseEntity
import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.details.ui.model.DetailsTab
import com.dezdeqness.details.ui.model.FranchiseReleaseUiModel
import com.dezdeqness.details.ui.model.ReleaseDetailsHeaderUiModel
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel

class ReleaseDetailsUiMapper(
    private val episodesUiMapper: EpisodesUiMapper,
) {

    fun map(item: ReleaseDetailsEntity, franchise: FranchiseEntity? = null): ReleaseDetailsUiModel {
        return ReleaseDetailsUiModel(
            id = item.id,
            header = ReleaseDetailsHeaderUiModel(
                id = item.id,
                title = item.name,
                season = item.season,
                year = item.year.toString(),
                imageUrl = item.poster,
            ),
            tabs = composeTabs(item, franchise),
        )
    }

    private fun composeTabs(
        entity: ReleaseDetailsEntity,
        franchise: FranchiseEntity?,
    ): List<DetailsTab> {
        return buildList {
            add(mapInfo(entity))
            add(mapEpisodes(entity))
            if (franchise != null) add(mapFranchise(franchise))
            add(mapStats(entity))
        }
    }

    private fun mapEpisodes(item: ReleaseDetailsEntity) = DetailsTab.EpisodesTab(
        episodes = item.episodes.map(episodesUiMapper::map)
    )

    private fun mapInfo(item: ReleaseDetailsEntity) = DetailsTab.InfoTab(
        summary = item.description,
        genres = item.genres,
        type = item.type,
        ageRating = item.ageRating,
        episodesTotal = item.episodesTotal,
        averageDuration = formatDuration(item.averageDuration),
        isOngoing = item.isOngoing,
    )

    private fun mapStats(item: ReleaseDetailsEntity) = DetailsTab.StatisticsTab(
        userFavourites = item.userFavourites,
        planned = item.planned,
        watched = item.watched,
        watching = item.watching,
        postponed = item.postponed,
        abandoned = item.abandoned,
    )

    private fun mapFranchise(franchise: FranchiseEntity) = DetailsTab.FranchiseTab(
        id = franchise.id,
        name = franchise.name,
        nameEnglish = franchise.nameEnglish,
        firstYear = franchise.firstYear,
        lastYear = franchise.lastYear,
        totalEpisodes = franchise.totalEpisodes,
        totalReleases = franchise.totalReleases,
        totalDuration = franchise.totalDuration,
        releases = franchise.franchiseReleases.map { release ->
            FranchiseReleaseUiModel(
                id = release.releaseId,
                sortOrder = release.sortOrder,
                title = release.release.name,
                imageUrl = release.release.poster,
                year = release.release.year,
                type = release.release.type,
            )
        },
    )

    private fun formatDuration(minutes: Long?): String {
        if (minutes == null) return "—"
        return "$minutes мин"
    }

}
