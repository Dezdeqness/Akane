package com.dezdeqness.analytics.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dezdeqness.analytics.data.db.AnalyticsDatabase
import java.io.File

fun getAnalyticsDatabaseBuilder(context: Context): RoomDatabase.Builder<AnalyticsDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("analytics.db")

    return Room.databaseBuilder<AnalyticsDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
