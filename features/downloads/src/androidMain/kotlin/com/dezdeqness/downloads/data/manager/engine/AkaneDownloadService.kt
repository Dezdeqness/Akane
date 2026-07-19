package com.dezdeqness.downloads.data.manager.engine

import android.app.Notification
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.scheduler.Requirements
import androidx.media3.exoplayer.scheduler.Scheduler
import com.dezdeqness.downloads.notification.AndroidDownloadNotifier
import com.dezdeqness.downloads.notification.DownloadNotificationTexts
import com.dezdeqness.downloads.utils.appForegroundIntent
import com.dezdeqness.downloads.utils.createNotificationChannel
import org.koin.mp.KoinPlatform
import androidx.media3.exoplayer.offline.DownloadManager as Media3DownloadManager
import androidx.media3.exoplayer.offline.DownloadService as Media3DownloadService

@UnstableApi
class AkaneDownloadService : Media3DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    null,
    0,
    0,
) {

    override fun onCreate() {
        createNotificationChannel(this)
        super.onCreate()
    }

    override fun getDownloadManager(): Media3DownloadManager =
        KoinPlatform.getKoin().get()

    override fun getScheduler(): Scheduler? = null

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: @Requirements.RequirementFlags Int,
    ): Notification {
        val builder = NotificationCompat.Builder(this, AndroidDownloadNotifier.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(appForegroundIntent())

        val activeDownloads = downloads.filter { it.state == Download.STATE_DOWNLOADING }
        val active = activeDownloads.firstOrNull() ?: return builder
            .setContentTitle(DownloadNotificationTexts.FOREGROUND_TITLE)
            .build()

        val labelLines = active.request.data
            .decodeToString()
            .takeIf { it.isNotBlank() }
            ?.lines()

        val title = labelLines?.getOrNull(0) ?: DownloadNotificationTexts.FOREGROUND_TITLE
        val episodeLabel = labelLines?.getOrNull(1)

        val percentValue = active.percentDownloaded
        val indeterminate = percentValue < 0
        val percent = percentValue.toInt().coerceIn(0, 100)

        val queuedCount = downloads.count { it.state == Download.STATE_QUEUED }
        val otherActiveCount = activeDownloads.size - 1
        val contentText = buildList {
            episodeLabel?.let(::add)
            if (!indeterminate) add("$percent%")
            if (otherActiveCount > 0) add("качается ещё $otherActiveCount")
            if (queuedCount > 0) add("в очереди: $queuedCount")
        }.joinToString(" · ")

        return builder
            .setContentTitle(title)
            .setContentText(contentText)
            .setProgress(MAX_PROGRESS, percent, indeterminate)
            .build()
    }

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 9_999
        private const val MAX_PROGRESS = 100
    }
}
