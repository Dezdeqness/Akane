package com.dezdeqness.analytics.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [AptabaseEventLocal::class],
    version = 1,
)
@ConstructedBy(AnalyticsDatabaseConstructor::class)
abstract class AnalyticsDatabase : RoomDatabase() {
    abstract fun aptabaseEventDao(): AptabaseEventDao
}

expect object AnalyticsDatabaseConstructor : RoomDatabaseConstructor<AnalyticsDatabase> {
    override fun initialize(): AnalyticsDatabase
}

fun getAnalyticsDatabase(builder: RoomDatabase.Builder<AnalyticsDatabase>): AnalyticsDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
