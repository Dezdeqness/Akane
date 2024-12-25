package com.dezdeqness.feed.domain.model

data class FeedEntity(
    val items: List<ReleaseEntity>,
    val page: Int,
    val hasNextPage: Boolean = true,
)
