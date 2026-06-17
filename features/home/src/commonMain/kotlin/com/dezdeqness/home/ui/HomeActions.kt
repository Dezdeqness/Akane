package com.dezdeqness.home.ui

import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel

interface HomeActions {
    fun onRetryClicked()
    fun onItemClicked(details: HomeUiModel)
    fun onGenreClicked(genre: GenrePanelUiModel)
    fun onAllGenresClicked()
}
