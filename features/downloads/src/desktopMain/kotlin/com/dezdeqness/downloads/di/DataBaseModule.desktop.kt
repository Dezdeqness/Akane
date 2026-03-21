package com.dezdeqness.downloads.di

import androidx.room.Room
import com.dezdeqness.downloads.data.db.DownloadDatabase
import com.dezdeqness.downloads.data.db.getDownloadDatabase
import org.koin.dsl.module
import java.io.File

actual fun dataBaseModule() = module {
    single<DownloadDatabase> {
        val userHome = System.getProperty("user.home")
        val dbFile = File(userHome, "downloads.db")

        val builder = Room.databaseBuilder<DownloadDatabase>(
            name = dbFile.absolutePath
        )
        getDownloadDatabase(builder)
    }
}
