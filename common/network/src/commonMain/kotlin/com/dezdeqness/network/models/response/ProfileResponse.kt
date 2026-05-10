package com.dezdeqness.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: Long,
    val login: String? = null,
    val email: String? = null,
    val nickname: String,
    val avatar: AvatarResponse? = null,
    val torrents: ProfileTorrentsResponse? = null,
    @SerialName("is_banned")
    val isBanned: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("is_with_ads")
    val isWithAds: Boolean = false,
)

@Serializable
data class AvatarResponse(
    val preview: String? = null,
    val thumbnail: String? = null,
    val optimized: ImageResponse? = null,
)

@Serializable
data class ImageResponse(
    val preview: String? = null,
    val thumbnail: String? = null,
)

@Serializable
data class ProfileTorrentsResponse(
    val passkey: String? = null,
    val uploaded: Long? = null,
    val downloaded: Long? = null,
)
