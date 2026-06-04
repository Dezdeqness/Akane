package com.dezdeqness.personal.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.personal.ui.model.PersonalUiModel

@Immutable
data class PersonalState(
    val list: List<PersonalUiModel> = listOf(),
    val status: Status = Status.Initial,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
)

enum class Status {
    Loaded,
    Loading,
    Empty,
    Error,
    Unauthorized,
    Initial,
}
