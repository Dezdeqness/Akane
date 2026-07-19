package com.dezdeqness.downloads.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.touchlab.kermit.Logger
import com.dezdeqness.downloads.utils.appForegroundIntent
import com.dezdeqness.downloads.utils.createNotificationChannel

class AndroidDownloadNotifier(
    private val context: Context,
) : DownloadNotifier {

    private val notificationManager: NotificationManagerCompat
        get() = NotificationManagerCompat.from(context)

    // no-op, service handles it
    override fun showProgress(info: DownloadNotificationInfo, progress: Float) = Unit

    override fun showCompleted(info: DownloadNotificationInfo) {
        val notification = baseBuilder(info)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentText(DownloadNotificationTexts.completedText(info))
            .setAutoCancel(true)
            .setGroup(groupKey(info.releaseId))
            .setContentIntent(context.appForegroundIntent())
            .build()

        notify(info.downloadId, notification)
        notifyGroupSummary(info)
    }

    override fun showFailed(info: DownloadNotificationInfo) {
        val notification = baseBuilder(info)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentText(DownloadNotificationTexts.failedText(info))
            .setAutoCancel(true)
            .setGroup(groupKey(info.releaseId))
            .setContentIntent(context.appForegroundIntent())
            .build()

        notify(info.downloadId, notification)
        notifyGroupSummary(info)
    }

    private fun notifyGroupSummary(info: DownloadNotificationInfo) {
        val summary = baseBuilder(info)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setGroup(groupKey(info.releaseId))
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setContentIntent(context.appForegroundIntent())
            .build()

        if (!notificationManager.areNotificationsEnabled()) return
        try {
            notificationManager.notify(summaryNotificationId(info.releaseId), summary)
        } catch (e: SecurityException) {
            Logger.w(TAG) { "Notification permission missing: ${e.message}" }
        }
    }



    private fun groupKey(releaseId: Long): String = "release_$releaseId"

    private fun summaryNotificationId(releaseId: Long): Int =
        SUMMARY_ID_BASE + (releaseId % NOTIFICATION_ID_RANGE).toInt()

    override fun dismiss(downloadId: Long) {
        notificationManager.cancel(notificationId(downloadId))
    }

    private fun baseBuilder(info: DownloadNotificationInfo): NotificationCompat.Builder {
        createNotificationChannel(context)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(DownloadNotificationTexts.title(info))
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }

    private fun notificationId(downloadId: Long): Int =
        NOTIFICATION_ID_BASE + (downloadId % NOTIFICATION_ID_RANGE).toInt()

    private fun notify(downloadId: Long, notification: android.app.Notification) {
        if (!notificationManager.areNotificationsEnabled()) return

        try {
            notificationManager.notify(notificationId(downloadId), notification)
        } catch (e: SecurityException) {
            Logger.w(TAG) { "Notification permission missing: ${e.message}" }
        }
    }

    companion object {
        const val CHANNEL_ID = "downloads"

        private const val TAG = "AndroidDownloadNotifier"
        private const val NOTIFICATION_ID_BASE = 10_000
        private const val NOTIFICATION_ID_RANGE = 10_000
        private const val SUMMARY_ID_BASE = 20_000
    }
}
