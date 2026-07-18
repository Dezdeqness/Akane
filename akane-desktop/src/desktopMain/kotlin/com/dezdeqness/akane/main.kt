package com.dezdeqness.akane

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dezdeqness.downloads.notification.DownloadNotificationActions
import com.dezdeqness.shared.App
import com.dezdeqness.shared.di.KoinModules
import org.koin.mp.KoinPlatform
import java.awt.Frame

object Akane {
    @JvmStatic
    fun main(args: Array<String>) = application {
        KoinModules.initKoinModules()
        Window(onCloseRequest = ::exitApplication, title = "Akane", icon = painterResource("ic_launcher.webp")) {
            val downloadNotificationActions = remember {
                KoinPlatform.getKoin().get<DownloadNotificationActions>()
            }
            LaunchedEffect(Unit) {
                downloadNotificationActions.actions.collect { action ->
                    when (action) {
                        DownloadNotificationActions.Action.AppForeground -> {
                            with(window) {
                               state = Frame.NORMAL
                               toFront()
                               requestFocus()
                            }
                        }
                    }
                }
            }
            App()
        }
    }
}
