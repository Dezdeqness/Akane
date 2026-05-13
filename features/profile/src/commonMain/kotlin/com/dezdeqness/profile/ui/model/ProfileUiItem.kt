package com.dezdeqness.profile.ui.model

data class ProfileUiItem(
    val nickname: String,
    val avatarUrl: String?,
    val joinedDate: String?,
) {
    val firstLetter = nickname.firstOrNull()?.toString()?.uppercase().orEmpty()
}
