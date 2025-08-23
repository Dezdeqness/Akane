package com.dezdeqness.personal.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<PersonalDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("personal.db")

    return Room.databaseBuilder<PersonalDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
