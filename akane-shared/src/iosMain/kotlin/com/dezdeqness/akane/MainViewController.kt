package com.dezdeqness.akane

import androidx.compose.ui.window.ComposeUIViewController
import com.dezdeqness.shared.App
import com.dezdeqness.shared.di.KoinModules

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinModules.initKoinModules()
    }
) { App() }
