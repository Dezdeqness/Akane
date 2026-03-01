package com.dezdeqness.akane

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dezdeqness.shared.App
import com.dezdeqness.shared.di.KoinModules

object Akane {
    @JvmStatic
    fun main(args: Array<String>) = application {
        KoinModules.initKoinModules()
        Window(onCloseRequest = ::exitApplication, title = "Akane") {
            App()
        }
    }
}
