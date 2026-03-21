package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.platform.DownloadDirectoryProvider
import com.dezdeqness.downloads.data.platform.VideoRemuxer
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DownloadDirectoryProvider(context = get()) }
    single { VideoRemuxer(context = get()) }
}
