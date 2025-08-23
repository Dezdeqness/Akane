package com.dezdeqness.personal.di

import com.dezdeqness.personal.data.db.PersonalDatabase
import com.dezdeqness.personal.data.db.getDatabaseBuilder
import com.dezdeqness.personal.data.db.getPersonalDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun dataBaseModule(): Module = module {
    single<PersonalDatabase> {
        val builder = getDatabaseBuilder()
        getPersonalDatabase(builder)
    }
}
