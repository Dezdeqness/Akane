package com.dezdeqness.profile.ui

import com.dezdeqness.profile.ui.model.ProfileUiItem

data class ProfileState(
    val profile: ProfileUiItem? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
