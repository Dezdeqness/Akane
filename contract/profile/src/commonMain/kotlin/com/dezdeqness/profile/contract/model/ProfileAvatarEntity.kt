package com.dezdeqness.profile.contract.model

data class ProfileAvatarEntity(
    val preview: String,
    val thumbnail: String,
    val optimized: ProfileImageEntity?,
)

data class ProfileImageEntity(
    val preview: String,
    val thumbnail: String,
)
