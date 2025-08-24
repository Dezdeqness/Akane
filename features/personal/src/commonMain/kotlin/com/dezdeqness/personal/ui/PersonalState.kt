package com.dezdeqness.personal.ui

import androidx.compose.runtime.Immutable
import com.dezdeqness.personal.ui.model.PersonalUiModel

@Immutable
data class PersonalState(
    val list: List<PersonalUiModel> = listOf(),
)
