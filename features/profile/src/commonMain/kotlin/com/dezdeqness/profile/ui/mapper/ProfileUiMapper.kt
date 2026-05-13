package com.dezdeqness.profile.ui.mapper

import com.dezdeqness.profile.contract.model.ProfileEntity
import com.dezdeqness.profile.ui.model.ProfileUiItem
import kotlinx.datetime.LocalDate

class ProfileUiMapper {

    fun map(entity: ProfileEntity) = ProfileUiItem(
        nickname = entity.nickname,
        avatarUrl = entity.avatar?.preview?.takeIf { it.isNotBlank() },
        joinedDate = formatJoinedDate(entity.createdAt),
    )

    private fun formatJoinedDate(raw: String): String? {
        if (raw.isBlank()) return null
        val datePart = raw.substringBefore('T')
        val date = runCatching { LocalDate.parse(datePart) }.getOrNull() ?: return null
        val day = date.day.toString().padStart(2, '0')
        val month = date.month.ordinal.plus(1).toString().padStart(2, '0')
        return "$day.$month.${date.year}"
    }
}
