package com.dezdeqness.catalog.contract.model

data class ReleasesPageEntity(
    val items: List<ReleaseEntity>,
    val currentPage: Int,
    val nextPage: Int,
    val hasNextPage: Boolean = true,
)
