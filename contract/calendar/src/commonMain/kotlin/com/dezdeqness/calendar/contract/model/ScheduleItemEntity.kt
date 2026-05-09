package com.dezdeqness.calendar.contract.model

data class ScheduleItemEntity(
    val id: Long,
    val name: String,
    val poster: String,
    val type: String,
    val description: String,
    val fullSeasonIsReleased: Boolean,
)
