package com.dezdeqness.feed.ui

import com.dezdeqness.feed.ui.model.FeedAnimeUiModel

data class FeedState(
    val items: List<FeedAnimeUiModel> = listOf(),
    val isLoading: Boolean = true,
)
