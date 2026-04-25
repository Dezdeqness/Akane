package com.dezdeqness.details.ui

import com.dezdeqness.details.domain.model.FranchiseEntity
import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.details.ui.model.DetailsTab
import com.dezdeqness.details.ui.model.FranchiseReleaseUiModel
import com.dezdeqness.details.ui.model.ReleaseDetailsHeaderUiModel
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel
import com.dezdeqness.downloads.domain.model.DownloadEntity

class ReleaseDetailsUiMapper(
    private val episodesUiMapper: EpisodesUiMapper,
) {

    fun map(
        item: ReleaseDetailsEntity,
        franchise: FranchiseEntity? = null,
        downloads: List<DownloadEntity> = emptyList(),
    ): MappedReleaseDetails {
        val tabs = composeTabs(item, franchise, downloads)
        val episodes = tabs.filterIsInstance<DetailsTab.EpisodesTab>()
            .firstOrNull()?.episodes.orEmpty()
        val commonQualities = episodes
            .map { it.episodeUrls.keys.toSet() }
            .reduceOrNull { acc, keys -> acc.intersect(keys) }
            ?.toList()
            .orEmpty()

        val details = ReleaseDetailsUiModel(
            id = item.id,
            header = ReleaseDetailsHeaderUiModel(
                id = item.id,
                title = item.name,
                season = item.season,
                year = item.year.toString(),
                imageUrl = item.poster,
            ),
            tabs = tabs,
        )
        return MappedReleaseDetails(details, commonQualities)
    }

    data class MappedReleaseDetails(
        val details: ReleaseDetailsUiModel,
        val commonQualities: List<String>,
    )

    private fun composeTabs(
        entity: ReleaseDetailsEntity,
        franchise: FranchiseEntity?,
        downloads: List<DownloadEntity>,
    ): List<DetailsTab> {
        return buildList {
            add(mapInfo(entity))
            add(mapEpisodes(entity, downloads))
            if (franchise != null) add(mapFranchise(franchise))
            add(mapStats(entity))
        }
    }

    private fun mapEpisodes(item: ReleaseDetailsEntity, downloads: List<DownloadEntity>) = DetailsTab.EpisodesTab(
        episodes = item.episodes.map { episodesUiMapper.map(it, downloads) }
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
        releases = franchise.franchiseReleases.sortedBy { it.sortOrder }.map { release ->
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
