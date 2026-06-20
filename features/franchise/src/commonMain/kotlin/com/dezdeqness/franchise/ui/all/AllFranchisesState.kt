package com.dezdeqness.franchise.ui.all

import androidx.compose.runtime.Immutable
import com.dezdeqness.franchise.ui.model.FranchiseUiModel

@Immutable
data class AllFranchisesState(
    val franchises: List<FranchiseUiModel> = listOf(),
    val status: AllFranchisesStatus = AllFranchisesStatus.Loading,
)

enum class AllFranchisesStatus {
    Loading,
    Error,
    Loaded,
}
