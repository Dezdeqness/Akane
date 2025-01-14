package com.dezdeqness.details.ui

import com.dezdeqness.details.ui.model.ReleaseDetailsUiModel

data class ReleaseDetailsState(
    val details: ReleaseDetailsUiModel? = null,
    val status: Status = Status.Initial,
)

enum class Status {
    Loaded,
    Loading,
    Initial,
    Error
}
