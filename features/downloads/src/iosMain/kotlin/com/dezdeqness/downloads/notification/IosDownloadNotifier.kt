package com.dezdeqness.downloads.notification

import co.touchlab.kermit.Logger
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

class IosDownloadNotifier : DownloadNotifier {

    private val presentationDelegate = ForegroundPresentationDelegate()

    private val notificationCenter: UNUserNotificationCenter
        get() = UNUserNotificationCenter.currentNotificationCenter()

    init {
        notificationCenter.delegate = presentationDelegate
    }

    override fun showProgress(info: DownloadNotificationInfo, progress: Float) = Unit

    override fun showCompleted(info: DownloadNotificationInfo) {
        post(
            downloadId = info.downloadId,
            title = DownloadNotificationTexts.title(info),
            body = DownloadNotificationTexts.completedText(info),
        )
    }

    override fun showFailed(info: DownloadNotificationInfo) {
        post(
            downloadId = info.downloadId,
            title = DownloadNotificationTexts.title(info),
            body = DownloadNotificationTexts.failedText(info),
        )
    }

    override fun dismiss(downloadId: Long) {
        val identifiers = listOf(identifier(downloadId))
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(identifiers)
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(identifiers)
    }

    private fun post(downloadId: Long, title: String, body: String) {
        val center = notificationCenter
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound,
        ) { granted, error ->
            if (!granted) {
                Logger.d(TAG) { "Notifications not authorized: ${error?.localizedDescription}" }
                return@requestAuthorizationWithOptions
            }

            val content = UNMutableNotificationContent().apply {
                setTitle(title)
                setBody(body)
                setSound(UNNotificationSound.defaultSound)
            }

            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = identifier(downloadId),
                content = content,
                trigger = null,
            )

            center.addNotificationRequest(request) { addError ->
                addError?.let { Logger.w(TAG) { "Failed to post notification: ${it.localizedDescription}" } }
            }
        }
    }

    private fun identifier(downloadId: Long): String = "download_$downloadId"

    private class ForegroundPresentationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {

        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
        ) {
            withCompletionHandler(
                UNNotificationPresentationOptionBanner or UNNotificationPresentationOptionSound
            )
        }
    }

    companion object {
        private const val TAG = "IosDownloadNotifier"
    }
}
