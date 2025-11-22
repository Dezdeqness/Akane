package com.dezdeqness.feed.ui

data class FeedFilterState(
    val sections: List<FilterSectionUiModel> = listOf(),
)

data class FilterSectionUiModel(
    val id: String,
    val displayName: String,
    val items: List<FilterCellUiModel>,
    val selectedCells: Set<String> = emptySet(),
    val sectionType: SectionType = SectionType.ChipMultipleChoice,
)

data class FilterCellUiModel(
    val id: String,
    val title: String,
)

enum class SectionType {
    ChipMultipleChoice,
    ChipSingleChoice,
    DropdownSingleChoice,
    DropdownMultiChoice,
    SectionSlider
}
