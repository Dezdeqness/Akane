package com.dezdeqness.profile.contract.model

data class ProfileEntity(
    val id: Long,
    val login: String,
    val email: String,
    val nickname: String,
    val avatar: ProfileAvatarEntity?,
    val torrents: ProfileTorrentsEntity?,
    val isBanned: Boolean,
    val createdAt: String,
    val isWithAds: Boolean,
)
