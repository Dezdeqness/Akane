package com.dezdeqness.franchise.ui.detail

import androidx.compose.runtime.Immutable
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel
import com.dezdeqness.franchise.ui.model.FranchiseHeaderUiModel

@Immutable
data class FranchiseDetailState(
    val header: FranchiseHeaderUiModel? = null,
    val items: List<ReleaseListUiModel> = listOf(),
    val status: FranchiseDetailStatus = FranchiseDetailStatus.Loading,
)

enum class FranchiseDetailStatus {
    Loading,
    Loaded,
    Empty,
    Error,
}
