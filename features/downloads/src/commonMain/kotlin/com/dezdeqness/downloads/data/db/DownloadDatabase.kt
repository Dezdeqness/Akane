package com.dezdeqness.downloads.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [DownloadEpisodeLocal::class, DownloadProgressLocal::class],
    version = 1,
)
@ConstructedBy(DownloadDatabaseConstructor::class)
abstract class DownloadDatabase : RoomDatabase() {
    abstract fun downloadEpisodeDao(): DownloadEpisodeDao
    abstract fun syncDownloadEpisodeDao(): SyncDownloadEpisodeDao
}

expect object DownloadDatabaseConstructor : RoomDatabaseConstructor<DownloadDatabase> {
    override fun initialize(): DownloadDatabase
}

fun getDownloadDatabase(builder: RoomDatabase.Builder<DownloadDatabase>): DownloadDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
