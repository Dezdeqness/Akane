package com.dezdeqness.views.di

import androidx.room.Room
import com.dezdeqness.views.data.db.TimecodeDatabase
import com.dezdeqness.views.data.db.getTimecodeDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual fun timecodeDataBaseModule(): Module = module {
    single<TimecodeDatabase> {
        val userHome = System.getProperty("user.home")
        val dbDirectory = File(userHome, ".akane/db/views").apply {
            mkdirs()
        }
        val dbFile = File(dbDirectory, "episode_timecode.db")

        val builder = Room.databaseBuilder<TimecodeDatabase>(
            name = dbFile.absolutePath,
        )
        getTimecodeDatabase(builder)
    }
}
