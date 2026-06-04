package com.dezdeqness.personal.ui

import com.dezdeqness.personal.ui.model.PersonalUiModel

interface PersonalActions {
    fun onItemClicked(details: PersonalUiModel)
    fun onEmptyListActionClicked()
    fun onLoadMore()
    fun onRetryClicked()
    fun onNavigateToProfile()
}
