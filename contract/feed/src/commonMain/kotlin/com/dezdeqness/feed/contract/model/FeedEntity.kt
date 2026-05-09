package com.dezdeqness.feed.contract.model

data class FeedEntity(
    val items: List<ReleaseEntity>,
    val currentPage: Int,
    val nextPage: Int,
    val hasNextPage: Boolean = true,
)
