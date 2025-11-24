package com.dezdeqness.feed.ui.filter

import com.dezdeqness.feed.core.DateUtils
import com.dezdeqness.feed.data.provider.GenreProvider
import com.dezdeqness.feed.domain.model.AgeRating
import com.dezdeqness.feed.domain.model.CatalogFilter
import com.dezdeqness.feed.domain.model.ProductionStatus
import com.dezdeqness.feed.domain.model.PublishStatus
import com.dezdeqness.feed.domain.model.ReleaseType
import com.dezdeqness.feed.domain.model.Season
import com.dezdeqness.feed.domain.model.Sorting

class FeedFilterMapper(
    private val genreProvider: GenreProvider,
) {

    fun mapFromFilter(filter: CatalogFilter): List<FilterSectionUiModel> {
        val genresSection = ChipFilterSectionUiModel(
            id = FeedFilterSectionIds.GENRES,
            displayName = "Жанры",
            items = genreProvider.getGenres().map { (id, title) ->
                FilterCellUiModel(
                    id = id.toString(),
                    title = title,
                )
            },
            selectedCells = filter.genres.map { it.toString() }.toSet(),
            description = null,
            displayType = FilterDisplayType.DIALOG_MULTI_CHOICE,
            placeholder = "Укажите жанры",
        )

        val typesSection = ChipFilterSectionUiModel(
            id = FeedFilterSectionIds.TYPES,
            displayName = "Тип",
            items = listOf(
                ReleaseType.TV to "TV",
                ReleaseType.ONA to "ONA",
                ReleaseType.WEB to "WEB",
                ReleaseType.OVA to "OVA",
                ReleaseType.OAD to "OAD",
                ReleaseType.MOVIE to "Фильм",
                ReleaseType.DORAMA to "Дорама",
                ReleaseType.SPECIAL to "Спешл",
            ).map { (type, title) ->
                FilterCellUiModel(
                    id = type.name,
                    title = title,
                )
            },
            selectedCells = filter.types.map(ReleaseType::name).toSet(),
            description = null,
            displayType = FilterDisplayType.CHIP_MULTI_CHOICE,
        )

        val seasonsSection = ChipFilterSectionUiModel(
            id = FeedFilterSectionIds.SEASONS,
            displayName = "Сезоны",
            items = listOf(
                Season.WINTER to "Зима",
                Season.SPRING to "Весна",
                Season.SUMMER to "Лето",
                Season.AUTUMN to "Осень",
            ).map { (season, title) ->
                FilterCellUiModel(
                    id = season.name,
                    title = title,
                )
            },
            selectedCells = filter.seasons.map(Season::name).toSet(),
            description = null,
            displayType = FilterDisplayType.CHIP_MULTI_CHOICE,
        )

        val sortingSection = ChipFilterSectionUiModel(
            id = FeedFilterSectionIds.SORTING,
            displayName = "Сортировка",
            items = Sorting.entries.map { sorting ->
                val title = when (sorting) {
                    Sorting.RATING_DESC -> "Рейтинг ↓"
                    Sorting.RATING_ASC -> "Рейтинг ↑"
                    Sorting.FRESH_AT_DESC -> "По новизне ↓"
                    Sorting.FRESH_AT_ASC -> "По новизне ↑"
                    Sorting.YEAR_DESC -> "Год ↓"
                    Sorting.YEAR_ASC -> "Год ↑"
                }
                FilterCellUiModel(
                    id = sorting.name,
                    title = title,
                )
            },
            selectedCells = filter.sorting?.let { setOf(it.name) } ?: emptySet(),
            description = null,
            displayType = FilterDisplayType.DIALOG_SINGLE_CHOICE,
            placeholder = "Укажите способ сортировки",
        )

        val ageRatingsSection = ChipFilterSectionUiModel(
            id = FeedFilterSectionIds.AGE_RATINGS,
            displayName = "Возрастной рейтинг",
            items = AgeRating.entries.map { rating ->
                val title = when (rating) {
                    AgeRating.R0_PLUS -> "+0"
                    AgeRating.R6_PLUS -> "+6"
                    AgeRating.R12_PLUS -> "+12"
                    AgeRating.R16_PLUS -> "+16"
                    AgeRating.R18_PLUS -> "+18"
                }
                FilterCellUiModel(
                    id = rating.name,
                    title = title,
                )
            },
            selectedCells = filter.ageRatings.map(AgeRating::name).toSet(),
            description = null,
            displayType = FilterDisplayType.CHIP_MULTI_CHOICE,
        )

        val currentYear = DateUtils.getCurrentYear()
        val yearsSection = SliderFilterSectionUiModel(
            id = FeedFilterSectionIds.DATE,
            displayName = "Год",
            minValue = 1995,
            maxValue = currentYear,
            currentStart = filter.fromYear ?: 1995,
            currentEnd = filter.toYear ?: currentYear,
            description = null,
        )

        val publishStatusSection = ChipFilterSectionUiModel(
            id = FeedFilterSectionIds.PUBLISH_STATUS,
            displayName = "Статус выхода",
            items = PublishStatus.entries.map { status ->
                val title = when (status) {
                    PublishStatus.IS_ONGOING -> "Онгоинг"
                    PublishStatus.IS_NOT_ONGOING -> "Вышло"
                }
                FilterCellUiModel(
                    id = status.name,
                    title = title,
                )
            },
            selectedCells = filter.publishStatus?.let { setOf(it.name) } ?: emptySet(),
            description = null,
            displayType = FilterDisplayType.CHIP_SINGLE_CHOICE,
        )

        val productionStatusSection = ChipFilterSectionUiModel(
            id = FeedFilterSectionIds.PRODUCTION_STATUS,
            displayName = "Статус озвучки",
            items = ProductionStatus.entries.map { status ->
                val title = when (status) {
                    ProductionStatus.IS_IN_PRODUCTION -> "В работе"
                    ProductionStatus.IS_NOT_IN_PRODUCTION -> "Готово"
                }
                FilterCellUiModel(
                    id = status.name,
                    title = title,
                )
            },
            selectedCells = filter.productionStatus?.let { setOf(it.name) } ?: emptySet(),
            description = null,
            displayType = FilterDisplayType.CHIP_SINGLE_CHOICE,
        )

        return listOf(
            genresSection,
            typesSection,
            seasonsSection,
            sortingSection,
            ageRatingsSection,
            yearsSection,
            publishStatusSection,
            productionStatusSection,
        )
    }

    fun mapToCatalogFilter(
        sections: List<FilterSectionUiModel>,
        catalogFilter: CatalogFilter,
    ): CatalogFilter {
        val genresSection =
            sections.findSectionById(FeedFilterSectionIds.GENRES) as? ChipFilterSectionUiModel
        val typesSection =
            sections.findSectionById(FeedFilterSectionIds.TYPES) as? ChipFilterSectionUiModel
        val seasonsSection =
            sections.findSectionById(FeedFilterSectionIds.SEASONS) as? ChipFilterSectionUiModel
        val sortingSection =
            sections.findSectionById(FeedFilterSectionIds.SORTING) as? ChipFilterSectionUiModel
        val ageRatingsSection =
            sections.findSectionById(FeedFilterSectionIds.AGE_RATINGS) as? ChipFilterSectionUiModel
        val yearsSection =
            sections.findSectionById(FeedFilterSectionIds.DATE) as? SliderFilterSectionUiModel
        val publishStatusSection =
            sections.findSectionById(FeedFilterSectionIds.PUBLISH_STATUS) as? ChipFilterSectionUiModel
        val productionStatusSection =
            sections.findSectionById(FeedFilterSectionIds.PRODUCTION_STATUS) as? ChipFilterSectionUiModel

        val genres = genresSection
            ?.selectedCells
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()

        val types = typesSection
            ?.selectedCells
            ?.mapNotNull { name -> runCatching { ReleaseType.valueOf(name) }.getOrNull() }
            ?: emptyList()

        val seasons = seasonsSection
            ?.selectedCells
            ?.mapNotNull { name -> runCatching { Season.valueOf(name) }.getOrNull() }
            ?: emptyList()

        val sorting = sortingSection
            ?.selectedCells
            ?.firstOrNull()
            ?.let { name -> runCatching { Sorting.valueOf(name) }.getOrNull() }

        val ageRatings = ageRatingsSection
            ?.selectedCells
            ?.mapNotNull { name -> runCatching { AgeRating.valueOf(name) }.getOrNull() }
            ?: emptyList()

        val fromYear = yearsSection?.currentStart
        val toYear = yearsSection?.currentEnd

        val publishStatus = publishStatusSection
            ?.selectedCells
            ?.firstOrNull()
            ?.let { name -> runCatching { PublishStatus.valueOf(name) }.getOrNull() }

        val productionStatus = productionStatusSection
            ?.selectedCells
            ?.firstOrNull()
            ?.let { name -> runCatching { ProductionStatus.valueOf(name) }.getOrNull() }

        return catalogFilter.copy(
            genres = genres,
            types = types,
            seasons = seasons,
            sorting = sorting,
            ageRatings = ageRatings,
            fromYear = fromYear,
            toYear = toYear,
            publishStatus = publishStatus,
            productionStatus = productionStatus,
        )
    }

    private fun List<FilterSectionUiModel>.findSectionById(id: String) =
        this.first { it.id == id }

}
