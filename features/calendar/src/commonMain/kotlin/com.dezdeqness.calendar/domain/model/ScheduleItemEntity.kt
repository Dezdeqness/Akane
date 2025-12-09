package com.dezdeqness.calendar.domain.model

data class ScheduleItemEntity(
    val id: Long,
    val name: String,
    val poster: String,
    val type: String,
    val description: String,
    val fullSeasonIsReleased: Boolean,
)
