package com.dezdeqness.views.di

import com.dezdeqness.views.data.db.TimecodeDatabase
import com.dezdeqness.views.data.db.getDatabaseBuilder
import com.dezdeqness.views.data.db.getTimecodeDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun timecodeDataBaseModule(): Module = module {
    single<TimecodeDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getTimecodeDatabase(builder)
    }
}
