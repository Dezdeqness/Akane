package com.dezdeqness.personal.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal")
data class PersonalLocal(
    @PrimaryKey
    val id: Long,
    val name: String,
    val poster: String,
    val createdTimeStamp: Long,
)
