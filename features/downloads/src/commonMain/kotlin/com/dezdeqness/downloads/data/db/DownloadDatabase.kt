package com.dezdeqness.downloads.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [DownloadEpisodeLocal::class, DownloadProgressLocal::class],
    version = 2,
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
        .addMigrations(MIGRATION_1_2)
        .build()
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE download_episodes_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                releaseId INTEGER NOT NULL,
                releaseTitle TEXT NOT NULL,
                episodeId TEXT NOT NULL,
                episodeName TEXT NOT NULL,
                episodeOrdinal REAL NOT NULL,
                quality TEXT NOT NULL,
                hlsUrl TEXT NOT NULL,
                filePath TEXT,
                createdAt INTEGER NOT NULL,
                previewUrl TEXT NOT NULL,
                openingStart INTEGER,
                openingEnd INTEGER,
                endingStart INTEGER,
                endingEnd INTEGER
            )
            """.trimIndent()
        )

        connection.execSQL(
            """
            INSERT INTO download_episodes_new
            SELECT
                id, releaseId, releaseTitle, episodeId, episodeName,
                CAST(episodeOrdinal AS REAL),
                quality, hlsUrl, filePath, createdAt, previewUrl,
                openingStart, openingEnd, endingStart, endingEnd
            FROM download_episodes
            """.trimIndent()
        )

        connection.execSQL("DROP TABLE download_episodes")
        connection.execSQL(
            "ALTER TABLE download_episodes_new RENAME TO download_episodes"
        )
    }
}