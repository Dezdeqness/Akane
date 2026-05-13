package com.dezdeqness.profile.data.mapper

import com.dezdeqness.network.models.response.AvatarResponse
import com.dezdeqness.network.models.response.ImageResponse
import com.dezdeqness.network.models.response.ProfileResponse
import com.dezdeqness.network.models.response.ProfileTorrentsResponse
import com.dezdeqness.profile.contract.model.ProfileAvatarEntity
import com.dezdeqness.profile.contract.model.ProfileEntity
import com.dezdeqness.profile.contract.model.ProfileImageEntity
import com.dezdeqness.profile.contract.model.ProfileTorrentsEntity

class ProfileMapper {

    fun mapProfile(response: ProfileResponse) = ProfileEntity(
        id = response.id,
        login = response.login.orEmpty(),
        email = response.email.orEmpty(),
        nickname = response.nickname,
        avatar = response.avatar?.let(::mapAvatar),
        torrents = response.torrents?.let(::mapTorrents),
        isBanned = response.isBanned,
        createdAt = response.createdAt.orEmpty(),
        isWithAds = response.isWithAds,
    )

    private fun mapAvatar(response: AvatarResponse) = ProfileAvatarEntity(
        preview = response.preview.orEmpty(),
        thumbnail = response.thumbnail.orEmpty(),
        optimized = response.optimized?.let(::mapImage),
    )

    private fun mapImage(response: ImageResponse) = ProfileImageEntity(
        preview = response.preview.orEmpty(),
        thumbnail = response.thumbnail.orEmpty(),
    )

    private fun mapTorrents(response: ProfileTorrentsResponse) = ProfileTorrentsEntity(
        passkey = response.passkey.orEmpty(),
        uploaded = response.uploaded ?: 0,
        downloaded = response.downloaded ?: 0,
    )
}
