package com.dezdeqness.downloads.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<DownloadDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("downloads.db")

    return Room.databaseBuilder<DownloadDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
