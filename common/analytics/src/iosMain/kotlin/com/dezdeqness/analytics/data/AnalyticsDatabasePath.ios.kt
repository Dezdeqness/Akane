package com.dezdeqness.analytics.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.dezdeqness.analytics.data.db.AnalyticsDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun getAnalyticsDatabaseBuilder(): RoomDatabase.Builder<AnalyticsDatabase> {
    val dbFilePath = documentDirectory() + "/analytics.db"
    return Room.databaseBuilder<AnalyticsDatabase>(
        name = dbFilePath,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )

    return requireNotNull(documentDirectory?.path)
}
