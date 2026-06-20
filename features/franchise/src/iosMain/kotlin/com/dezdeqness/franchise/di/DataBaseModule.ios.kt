package com.dezdeqness.franchise.di

import com.dezdeqness.franchise.data.db.FranchiseDatabase
import com.dezdeqness.franchise.data.db.getDatabaseBuilder
import com.dezdeqness.franchise.data.db.getFranchiseDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun dataBaseModule(): Module = module {
    single<FranchiseDatabase> {
        val builder = getDatabaseBuilder()
        getFranchiseDatabase(builder)
    }
}
