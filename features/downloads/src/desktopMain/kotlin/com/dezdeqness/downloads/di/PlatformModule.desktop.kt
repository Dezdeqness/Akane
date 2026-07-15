package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.platform.DownloadDirectoryProvider
import org.koin.dsl.module

actual fun platformModule() = module {
    single { DownloadDirectoryProvider() }
}
