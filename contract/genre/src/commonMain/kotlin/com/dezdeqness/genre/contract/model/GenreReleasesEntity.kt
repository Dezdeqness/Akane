package com.dezdeqness.genre.contract.model

import com.dezdeqness.feed.contract.model.ReleaseEntity

data class GenreReleasesEntity(
    val items: List<ReleaseEntity>,
    val currentPage: Int,
    val nextPage: Int,
    val hasNextPage: Boolean = true,
)
