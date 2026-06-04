package com.dezdeqness.details.ui

import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.details.ui.model.FavouriteButtonState
import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel

data class ReleaseDetailsState(
    val details: ReleaseDetailsUiModel? = null,
    val status: Status = Status.Initial,
    val favouriteButtonState: FavouriteButtonState = FavouriteButtonState.Loaded(isFavourite = false),
    val dialogState: DownloadDialogState = DownloadDialogState.None,
    val commonQualities: List<String> = emptyList(),
)

enum class Status {
    Loaded,
    Loading,
    Initial,
    Error,
}

sealed interface DownloadDialogState {
    data object None : DownloadDialogState
    data class SingleEpisode(val episode: EpisodesUiModel) : DownloadDialogState
    data object BatchQuality : DownloadDialogState
}

