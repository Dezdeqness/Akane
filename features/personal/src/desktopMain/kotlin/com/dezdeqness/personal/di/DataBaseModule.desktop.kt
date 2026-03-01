package com.dezdeqness.personal.di

import androidx.room.Room
import com.dezdeqness.personal.data.db.PersonalDatabase
import com.dezdeqness.personal.data.db.getPersonalDatabase
import org.koin.dsl.module
import java.io.File

actual fun dataBaseModule() = module {
    single<PersonalDatabase> {

        val userHome = System.getProperty("user.home")
        val dbFile = File(userHome, "personal.db")

        val builder = Room.databaseBuilder<PersonalDatabase>(
            name = dbFile.absolutePath
        )
        getPersonalDatabase(builder)

    }
}
