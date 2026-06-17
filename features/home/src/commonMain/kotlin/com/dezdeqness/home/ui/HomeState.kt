package com.dezdeqness.home.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel

@Immutable
data class HomeState(
    val bestRated: List<HomeUiModel> = listOf(),
    val released: List<HomeUiModel> = listOf(),
    val onGoing: List<HomeUiModel> = listOf(),
    val freshUpdates: List<HomeUiModel> = listOf(),
    val genres: List<GenrePanelUiModel> = listOf(),
    val status: StateStatus = StateStatus.Initial,
)

enum class StateStatus {
    Initial,
    Loading,
    Error,
    Loaded
}
