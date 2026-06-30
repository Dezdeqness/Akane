package com.dezdeqness.views.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [TimecodeLocal::class],
    version = 1,
)
@ConstructedBy(TimecodeDatabaseConstructor::class)
abstract class TimecodeDatabase : RoomDatabase() {
    abstract fun timecodeDao(): TimecodeDao
}

expect object TimecodeDatabaseConstructor : RoomDatabaseConstructor<TimecodeDatabase> {
    override fun initialize(): TimecodeDatabase
}

fun getTimecodeDatabase(builder: RoomDatabase.Builder<TimecodeDatabase>): TimecodeDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
