package com.dezdeqness.feed.ui.filter

import androidx.lifecycle.ViewModel
import com.dezdeqness.feed.domain.model.CatalogFilter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class FeedFilterViewModel(
    private val feedFilterMapper: FeedFilterMapper,
) : ViewModel() {


    private val _events = Channel<FeedFilterEvent>()
    val events = _events.receiveAsFlow()

    private val _sectionsState = MutableStateFlow(FeedFilterState())
    val sectionsState: StateFlow<FeedFilterState> = _sectionsState

    private var currentCatalogFilter: CatalogFilter = CatalogFilter()

    fun onFeedFilterShown(catalogFilter: CatalogFilter) {
        currentCatalogFilter = catalogFilter
        _sectionsState.update {
            it.copy(sections = feedFilterMapper.mapFromFilter(catalogFilter))
        }
    }

    fun onAction(action: FeedFilterSectionAction) {
        when (action) {
            is FeedFilterSectionAction.ToggleItem -> {
                handleToggleItem(action.sectionId, action.itemId)
            }

            is FeedFilterSectionAction.UpdateSelectedItems -> {
                handleUpdateSelectedItems(action.sectionId, action.selectedIds)
            }

            is FeedFilterSectionAction.UpdateRange -> {
                handleUpdateRange(action.sectionId, action.start, action.end)
            }

            FeedFilterSectionAction.Reset -> {
                handleReset()
            }

            FeedFilterSectionAction.Apply -> {
                handleApply()
            }
        }
    }

    private fun handleToggleItem(sectionId: String, itemId: String) {
        _sectionsState.update { state ->
            val updatedSections = state.sections.map { section ->
                if (section.id != sectionId) return@map section

                when (section) {
                    is ChipFilterSectionUiModel -> {
                        val newSelected = when (section.displayType) {
                            FilterDisplayType.CHIP_MULTI_CHOICE,
                            FilterDisplayType.DIALOG_MULTI_CHOICE,
                                -> {
                                if (itemId in section.selectedCells) {
                                    section.selectedCells - itemId
                                } else {
                                    section.selectedCells + itemId
                                }
                            }

                            FilterDisplayType.CHIP_SINGLE_CHOICE,
                            FilterDisplayType.DIALOG_SINGLE_CHOICE,
                                -> {
                                if (itemId in section.selectedCells) {
                                    emptySet()
                                } else {
                                    setOf(itemId)
                                }
                            }
                        }
                        section.copy(selectedCells = newSelected)
                    }

                    else -> section
                }
            }
            state.copy(sections = updatedSections)
        }
    }

    private fun handleUpdateSelectedItems(sectionId: String, selectedIds: Set<String>) {
        _sectionsState.update { state ->
            val updatedSections = state.sections.map { section ->
                if (section.id != sectionId) return@map section

                when (section) {
                    is ChipFilterSectionUiModel -> {
                        section.copy(selectedCells = selectedIds)
                    }

                    else -> section
                }
            }
            state.copy(sections = updatedSections)
        }
    }

    private fun handleUpdateRange(sectionId: String, start: Int, end: Int) {
        _sectionsState.update { state ->
            val updatedSections = state.sections.map { section ->
                if (section.id != sectionId) return@map section

                when (section) {
                    is SliderFilterSectionUiModel -> {
                        section.copy(currentStart = start, currentEnd = end)
                    }

                    else -> section
                }
            }
            state.copy(sections = updatedSections)
        }
    }

    private fun handleReset() {
        _sectionsState.update { state ->
            val updatedSections = state.sections.map { section ->
                when (section) {
                    is ChipFilterSectionUiModel -> {
                        section.copy(selectedCells = emptySet())
                    }

                    is SliderFilterSectionUiModel -> {
                        section.copy(
                            currentStart = section.minValue,
                            currentEnd = section.maxValue
                        )
                    }
                }
            }
            state.copy(sections = updatedSections)
        }
    }

    private fun handleApply() {
        val updatedFilter = feedFilterMapper.mapToCatalogFilter(
            sections = _sectionsState.value.sections,
            catalogFilter = currentCatalogFilter
        )
        currentCatalogFilter = updatedFilter
        _events.trySend(FeedFilterEvent.ApplyFilter(updatedFilter))
    }
}
