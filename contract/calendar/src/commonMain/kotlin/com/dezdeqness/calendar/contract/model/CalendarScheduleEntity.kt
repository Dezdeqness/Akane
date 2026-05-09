package com.dezdeqness.calendar.contract.model

data class CalendarScheduleEntity(
    val today: List<ScheduleItemEntity>,
    val tomorrow: List<ScheduleItemEntity>,
    val yesterday: List<ScheduleItemEntity>,
)
