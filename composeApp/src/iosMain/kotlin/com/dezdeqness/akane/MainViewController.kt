package com.dezdeqness.akane

import androidx.compose.ui.window.ComposeUIViewController
import com.dezdeqness.feed.di.feedModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(feedModule)
        }
    }
) { App() }
