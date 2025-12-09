package com.dezdeqness.calendar.domain.model

data class CalendarScheduleEntity(
    val today: List<ScheduleItemEntity>,
    val tomorrow: List<ScheduleItemEntity>,
    val yesterday: List<ScheduleItemEntity>,
)
