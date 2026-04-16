package com.dezdeqness.analytics.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.dezdeqness.analytics.data.db.AnalyticsDatabase
import java.io.File

fun getAnalyticsDatabaseBuilder(): RoomDatabase.Builder<AnalyticsDatabase> {
    val userHome = System.getProperty("user.home")
    val dbDirectory = File(userHome, ".akane/db/analytics").apply {
        mkdirs()
    }
    val dbFile = File(dbDirectory, "analytics.db")

    return Room.databaseBuilder<AnalyticsDatabase>(
        name = dbFile.absolutePath
    )
}
