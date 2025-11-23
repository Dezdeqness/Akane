package com.dezdeqness.feed.ui.filter

import com.dezdeqness.feed.domain.model.CatalogFilter

data class FeedFilterState(
    val sections: List<FilterSectionUiModel> = listOf(),
)

sealed interface FeedFilterEvent {
    data class ApplyFilter(val catalogFilter: CatalogFilter) : FeedFilterEvent
}

sealed interface FilterSectionUiModel {
    val id: String
    val displayName: String
    val description: String?
}

enum class FilterDisplayType {
    CHIP_SINGLE_CHOICE,
    CHIP_MULTI_CHOICE,
    DIALOG_SINGLE_CHOICE,
    DIALOG_MULTI_CHOICE,
}

data class ChipFilterSectionUiModel(
    override val id: String,
    override val displayName: String,
    override val description: String?,
    val displayType: FilterDisplayType,
    val placeholder: String? = null,
    val items: List<FilterCellUiModel>,
    val selectedCells: Set<String> = emptySet(),
) : FilterSectionUiModel

data class SliderFilterSectionUiModel(
    override val id: String,
    override val displayName: String,
    override val description: String?,
    val minValue: Int,
    val maxValue: Int,
    val currentStart: Int,
    val currentEnd: Int,
) : FilterSectionUiModel

data class FilterCellUiModel(
    val id: String,
    val title: String,
)

object FeedFilterSectionIds {
    const val TYPES = "types"
    const val SEASONS = "seasons"
    const val GENRES = "genres"
    const val DATE = "date"
    const val SORTING = "sorting"
    const val AGE_RATINGS = "age_ratings"
    const val PUBLISH_STATUS = "publish_status"
    const val PRODUCTION_STATUS = "production_status"
}
