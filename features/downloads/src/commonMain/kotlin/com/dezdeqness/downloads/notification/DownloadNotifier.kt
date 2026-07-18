package com.dezdeqness.downloads.notification

interface DownloadNotifier {

    fun showProgress(info: DownloadNotificationInfo, progress: Float)

    fun showCompleted(info: DownloadNotificationInfo)

    fun showFailed(info: DownloadNotificationInfo)

    fun dismiss(downloadId: Long)
}
