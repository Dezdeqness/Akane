package com.dezdeqness.akane

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dezdeqness.details.di.detailsModule
import com.dezdeqness.feed.di.feedModule
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(feedModule)
        modules(detailsModule)
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Akane",
    ) {
        App()
    }
}