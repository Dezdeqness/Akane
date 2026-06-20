package com.dezdeqness.franchise.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "franchise_cache")
data class FranchiseLocal(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameEnglish: String,
    val imageUrl: String,
    val rating: Double,
    val firstYear: Int,
    val lastYear: Int,
    val totalReleases: Int,
    val totalEpisodes: Int,
    val totalDuration: String?,
    val totalDurationInSeconds: Int,
)
