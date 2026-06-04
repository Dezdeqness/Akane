package com.dezdeqness.personal.data.db

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `personal_new` (`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("INSERT OR IGNORE INTO `personal_new` (`id`) SELECT `id` FROM `personal`")
        connection.execSQL("DROP TABLE `personal`")
        connection.execSQL("ALTER TABLE `personal_new` RENAME TO `personal`")
    }
}
