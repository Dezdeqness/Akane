package com.dezdeqness.downloads.di

import com.dezdeqness.downloads.data.db.DownloadDatabase
import com.dezdeqness.downloads.data.db.getDatabaseBuilder
import com.dezdeqness.downloads.data.db.getDownloadDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun dataBaseModule(): Module = module {
    single<DownloadDatabase> {
        val builder = getDatabaseBuilder()
        getDownloadDatabase(builder)
    }
}
