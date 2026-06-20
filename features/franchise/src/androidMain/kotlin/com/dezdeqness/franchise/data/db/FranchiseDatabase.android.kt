package com.dezdeqness.franchise.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<FranchiseDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("franchise_cache.db")

    return Room.databaseBuilder<FranchiseDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
