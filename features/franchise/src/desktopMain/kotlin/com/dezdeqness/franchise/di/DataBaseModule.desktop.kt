package com.dezdeqness.franchise.di

import androidx.room.Room
import com.dezdeqness.franchise.data.db.FranchiseDatabase
import com.dezdeqness.franchise.data.db.getFranchiseDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual fun dataBaseModule(): Module = module {
    single<FranchiseDatabase> {
        val userHome = System.getProperty("user.home")
        val dbDirectory = File(userHome, ".akane/db/franchise").apply {
            mkdirs()
        }
        val dbFile = File(dbDirectory, "franchise_cache.db")

        val builder = Room.databaseBuilder<FranchiseDatabase>(
            name = dbFile.absolutePath,
        )
        getFranchiseDatabase(builder)
    }
}
