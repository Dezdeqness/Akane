package com.dezdeqness.profile.contract.model

data class ProfileTorrentsEntity(
    val passkey: String,
    val uploaded: Long,
    val downloaded: Long,
)
