package com.dezdeqness.downloads.notification

import co.touchlab.kermit.Logger
import java.awt.Color
import java.awt.EventQueue
import java.awt.Image
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class DesktopTrayDownloadNotifier(
    private val actions: DownloadNotificationActions,
) : DownloadNotifier {

    private var trayIcon: TrayIcon? = null

    override fun showProgress(info: DownloadNotificationInfo, progress: Float) {
        val percent = (progress * 100).toInt()
        onTray { icon ->
            icon.toolTip = "$APP_NAME — ${DownloadNotificationTexts.title(info)}: " +
                    DownloadNotificationTexts.progressText(info, percent)
        }
    }

    override fun showCompleted(info: DownloadNotificationInfo) {
        onTray { icon ->
            icon.toolTip = APP_NAME
            icon.displayMessage(
                DownloadNotificationTexts.title(info),
                DownloadNotificationTexts.completedText(info),
                TrayIcon.MessageType.INFO,
            )
        }
    }

    override fun showFailed(info: DownloadNotificationInfo) {
        onTray { icon ->
            icon.toolTip = APP_NAME
            icon.displayMessage(
                DownloadNotificationTexts.title(info),
                DownloadNotificationTexts.failedText(info),
                TrayIcon.MessageType.ERROR,
            )
        }
    }

    override fun dismiss(downloadId: Long) {
        onTray { icon ->
            icon.toolTip = APP_NAME
        }
    }

    private fun onTray(block: (TrayIcon) -> Unit) {
        if (!SystemTray.isSupported()) return

        EventQueue.invokeLater {
            val icon = obtainTrayIcon() ?: return@invokeLater
            runCatching { block(icon) }
                .onFailure { Logger.w(TAG) { "Tray notification failed: ${it.message}" } }
        }
    }

    private fun obtainTrayIcon(): TrayIcon? {
        trayIcon?.let { return it }

        return runCatching {
            TrayIcon(createTrayImage(), APP_NAME).apply {
                isImageAutoSize = true
                addActionListener { actions.openForeground() }
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (e.button == MouseEvent.BUTTON1) {
                            actions.openForeground()
                        }
                    }
                })
                SystemTray.getSystemTray().add(this)
            }
        }.onFailure {
            Logger.w(TAG) { "Failed to add tray icon: ${it.message}" }
        }.getOrNull()?.also { trayIcon = it }
    }

    private fun createTrayImage(): Image =
        loadAppIcon() ?: createFallbackImage()

    private fun loadAppIcon(): Image? = runCatching {
        javaClass.getResourceAsStream(TRAY_ICON_RESOURCE)?.use(ImageIO::read)
    }.onFailure {
        Logger.w(TAG) { "Failed to load tray icon resource: ${it.message}" }
    }.getOrNull()

    private fun createFallbackImage(): Image {
        val size = TRAY_IMAGE_SIZE
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()
        graphics.color = Color(0xE9, 0x4F, 0x64)
        graphics.fillOval(0, 0, size, size)
        graphics.dispose()
        return image
    }

    companion object {
        private const val TAG = "DesktopTrayDownloadNotifier"
        private const val APP_NAME = "Akane"
        private const val TRAY_ICON_RESOURCE = "/download_tray_icon.png"
        private const val TRAY_IMAGE_SIZE = 16
    }
}
