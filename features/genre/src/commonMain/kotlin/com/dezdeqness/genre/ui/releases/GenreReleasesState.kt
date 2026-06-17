package com.dezdeqness.genre.ui.releases

import androidx.compose.runtime.Immutable
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel

@Immutable
data class GenreReleasesState(
    val items: List<ReleaseListUiModel> = listOf(),
    val status: GenreReleasesStatus = GenreReleasesStatus.Loading,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
)

enum class GenreReleasesStatus {
    Loading,
    Loaded,
    Empty,
    Error,
}
