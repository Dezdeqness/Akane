package com.dezdeqness.franchise.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [FranchiseLocal::class],
    version = 1,
)
@ConstructedBy(FranchiseDatabaseConstructor::class)
abstract class FranchiseDatabase : RoomDatabase() {
    abstract fun franchiseCacheDao(): FranchiseCacheDao
}

expect object FranchiseDatabaseConstructor : RoomDatabaseConstructor<FranchiseDatabase> {
    override fun initialize(): FranchiseDatabase
}

fun getFranchiseDatabase(builder: RoomDatabase.Builder<FranchiseDatabase>): FranchiseDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
