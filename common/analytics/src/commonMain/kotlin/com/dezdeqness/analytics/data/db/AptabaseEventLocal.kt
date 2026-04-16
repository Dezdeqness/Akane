package com.dezdeqness.analytics.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aptabase_events")
data class AptabaseEventLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val payload: String,
    val createdAtMillis: Long,
)
