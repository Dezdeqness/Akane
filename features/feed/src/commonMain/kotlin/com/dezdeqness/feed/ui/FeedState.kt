package com.dezdeqness.feed.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel
import com.dezdeqness.feed.contract.model.CatalogFilter

@Immutable
data class FeedState(
    val items: List<ReleaseListUiModel> = listOf(),
    val status: Status = Status.Initial,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
    val input: FeedUserInput = FeedUserInput(),
)

@Immutable
data class FeedUserInput(
    val filterCatalogFilter: CatalogFilter = CatalogFilter(),
) {
    val search get() = filterCatalogFilter.search
}

enum class Status {
    Loaded,
    Loading,
    Empty,
    Initial,
    Error
}
