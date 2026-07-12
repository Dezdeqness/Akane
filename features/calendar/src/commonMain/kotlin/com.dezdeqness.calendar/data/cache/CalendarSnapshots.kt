package com.dezdeqness.calendar.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class CalendarScheduleSnapshot(
    val today: List<ScheduleItemSnapshot>,
    val tomorrow: List<ScheduleItemSnapshot>,
    val yesterday: List<ScheduleItemSnapshot>,
)

@Serializable
data class ScheduleItemSnapshot(
    val id: Long,
    val name: String,
    val poster: String,
    val type: String,
    val description: String,
    val fullSeasonIsReleased: Boolean,
)
