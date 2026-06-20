package com.dezdeqness.home.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.home.ui.model.FranchisePanelUiModel
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel
import com.dezdeqness.home.ui.model.PromoPanelUiModel

@Immutable
data class HomeState(
    val promos: List<PromoPanelUiModel> = listOf(),
    val freshUpdates: List<HomeUiModel> = listOf(),
    val onGoing: List<HomeUiModel> = listOf(),
    val franchises: List<FranchisePanelUiModel> = listOf(),
    val released: List<HomeUiModel> = listOf(),
    val bestRated: List<HomeUiModel> = listOf(),
    val genres: List<GenrePanelUiModel> = listOf(),
    val status: StateStatus = StateStatus.Initial,
)

enum class StateStatus {
    Initial,
    Loading,
    Error,
    LoadingMore,
    SecondPartError,
    Loaded,
}
