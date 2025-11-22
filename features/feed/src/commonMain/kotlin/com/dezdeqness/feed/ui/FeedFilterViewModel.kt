package com.dezdeqness.feed.ui

import androidx.lifecycle.ViewModel
import com.dezdeqness.feed.domain.model.CatalogFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FeedFilterViewModel(
    private val feedFilterMapper: FeedFilterMapper,
) : ViewModel() {

    private val _sectionsState =
        MutableStateFlow(FeedFilterState())
    val sectionsState: StateFlow<FeedFilterState> = _sectionsState

    fun onFeedFilterShown(catalogFilter: CatalogFilter) {
        _sectionsState.update {
            it.copy(sections = feedFilterMapper.mapFromFilter(catalogFilter))
        }
    }
}
