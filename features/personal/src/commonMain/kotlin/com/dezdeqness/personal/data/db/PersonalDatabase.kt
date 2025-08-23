package com.dezdeqness.personal.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.dezdeqness.personal.data.models.PersonalLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [PersonalLocal::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class PersonalDatabase : RoomDatabase() {
    abstract fun personalDao(): PersonalDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<PersonalDatabase> {
    override fun initialize(): PersonalDatabase
}

fun getPersonalDatabase(builder: RoomDatabase.Builder<PersonalDatabase>): PersonalDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
