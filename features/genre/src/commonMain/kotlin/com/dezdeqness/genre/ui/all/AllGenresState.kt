package com.dezdeqness.genre.ui.all

import androidx.compose.runtime.Immutable
import com.dezdeqness.genre.ui.model.GenreUiModel

@Immutable
data class AllGenresState(
    val genres: List<GenreUiModel> = listOf(),
    val status: AllGenresStatus = AllGenresStatus.Loading,
)

enum class AllGenresStatus {
    Loading,
    Error,
    Loaded,
}
