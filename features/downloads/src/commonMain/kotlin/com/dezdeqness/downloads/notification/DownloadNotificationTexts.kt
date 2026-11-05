package com.dezdeqness.downloads.notification

import com.dezdeqness.foundation.utils.toEpisodeOrdinalString

object DownloadNotificationTexts {

    const val CHANNEL_NAME = "Загрузки"
    const val CHANNEL_DESCRIPTION = "Прогресс загрузки эпизодов"
    const val FOREGROUND_TITLE = "Загрузка эпизодов"

    fun title(info: DownloadNotificationInfo): String = info.releaseTitle

    fun progressText(info: DownloadNotificationInfo, percent: Int): String =
        "Серия ${info.episodeOrdinal.toEpisodeOrdinalString()} — $percent%"

    fun completedText(info: DownloadNotificationInfo): String =
        "Серия ${info.episodeOrdinal.toEpisodeOrdinalString()} загружена"

    fun failedText(info: DownloadNotificationInfo): String =
        "Не удалось загрузить серию ${info.episodeOrdinal.toEpisodeOrdinalString()}"
}
