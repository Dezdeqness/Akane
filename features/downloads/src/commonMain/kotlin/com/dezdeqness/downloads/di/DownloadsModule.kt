package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.ui.activedownloads.ActiveDownloadsViewModel
import com.dezdeqness.downloads.ui.episodes.ReleaseEpisodesViewModel
import com.dezdeqness.downloads.ui.library.LibraryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val downloadsModule = module {
    includes(dataBaseModule(), platformModule(), dataModule, domainModule)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::ActiveDownloadsViewModel)
    viewModelOf(::ReleaseEpisodesViewModel)
}
