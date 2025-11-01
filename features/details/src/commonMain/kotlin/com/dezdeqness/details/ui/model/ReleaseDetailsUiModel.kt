package com.dezdeqness.details.ui.model

data class ReleaseDetailsUiModel(
    val id: Long,
    val header: ReleaseDetailsHeaderUiModel,
    val tabs: List<DetailsTab>,
)

data class ReleaseDetailsHeaderUiModel(
    val id: Long,
    val title: String,
    val season: String,
    val year: String,
    val imageUrl: String,
)

sealed class DetailsTab(val title: String) {
    data class InfoTab(
        val summary: String,
        val genres: List<String>,
        val type: String,
        val ageRating: String,
        val episodesTotal: Long,
        val averageDuration: String,
        val isOngoing: Boolean,
    ) : DetailsTab("Информация")

    data class EpisodesTab(val episodes: List<EpisodesUiModel>) : DetailsTab("Эпизоды")

    data class FranchiseTab(
        val id: String,
        val name: String,
        val nameEnglish: String,
        val firstYear: Long,
        val lastYear: Long,
        val totalEpisodes: Long,
        val totalReleases: Long,
        val totalDuration: String,
        val releases: List<FranchiseReleaseUiModel>,
    ) : DetailsTab("Франшиза")

    data class StatisticsTab(
        val userFavourites: Long,
        val planned: Long,
        val watched: Long,
        val watching: Long,
        val postponed: Long,
        val abandoned: Long,
    ) : DetailsTab("Статистика")

}

data class FranchiseReleaseUiModel(
    val id: Long,
    val sortOrder: Long,
    val title: String,
    val imageUrl: String,
    val year: Long,
    val type: String,
)
