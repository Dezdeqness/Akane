package com.dezdeqness.feed.ui.filter

sealed interface FeedFilterSectionAction {

    data object Reset : FeedFilterSectionAction
    data object Apply : FeedFilterSectionAction

    data class ToggleItem(
        val sectionId: String,
        val itemId: String,
    ) : FeedFilterSectionAction

    data class UpdateSelectedItems(
        val sectionId: String,
        val selectedIds: Set<String>,
    ) : FeedFilterSectionAction

    data class UpdateRange(
        val sectionId: String,
        val start: Int,
        val end: Int,
    ) : FeedFilterSectionAction
}
