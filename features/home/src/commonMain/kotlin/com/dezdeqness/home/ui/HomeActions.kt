package com.dezdeqness.home.ui

import com.dezdeqness.home.ui.model.ContinueWatchingUiModel
import com.dezdeqness.home.ui.model.FranchisePanelUiModel
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel

interface HomeActions {
    fun onItemClicked(details: HomeUiModel)
    fun onContinueWatchingClicked(item: ContinueWatchingUiModel)
    fun onGenreClicked(genre: GenrePanelUiModel)
    fun onAllGenresClicked()
    fun onPromoReleaseClicked(releaseId: Long, title: String)
    fun onFranchiseClicked(franchise: FranchisePanelUiModel)
    fun onAllFranchisesClicked()
    fun onRetryClicked()
}
