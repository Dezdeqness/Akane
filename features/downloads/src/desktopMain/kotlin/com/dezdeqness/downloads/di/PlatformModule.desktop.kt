package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.platform.DownloadDirectoryProvider
import com.dezdeqness.downloads.data.platform.VideoRemuxer
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun platformModule() = module {
    single { DownloadDirectoryProvider() }
    single { VideoRemuxer(coroutineDispatcherProvider = get()) }
    single(named("remuxEnabled")) { true }
}
