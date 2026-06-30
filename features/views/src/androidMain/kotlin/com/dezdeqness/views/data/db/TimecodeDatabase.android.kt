package com.dezdeqness.views.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TimecodeDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("episode_timecode.db")

    return Room.databaseBuilder<TimecodeDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
