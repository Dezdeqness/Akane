package com.dezdeqness.downloads.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.dezdeqness.downloads.notification.AndroidDownloadNotifier.Companion.CHANNEL_ID
import com.dezdeqness.downloads.notification.DownloadNotificationTexts

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW,
        )
            .setName(DownloadNotificationTexts.CHANNEL_NAME)
            .setDescription(DownloadNotificationTexts.CHANNEL_DESCRIPTION)
            .build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}

fun Context.appForegroundIntent(): PendingIntent? {
    val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: return null
    launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

    return PendingIntent.getActivity(
        this,
        4_001,
        launchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}