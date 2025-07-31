package com.dezdeqness.feed.ui

import com.dezdeqness.feed.ui.model.FeedAnimeUiModel

data class FeedState(
    val items: List<FeedAnimeUiModel> = listOf(),
    val status: Status = Status.Initial,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
)
enum class Status {
    Loaded,
    Loading,
    Empty,
    Initial,
    Error
}
