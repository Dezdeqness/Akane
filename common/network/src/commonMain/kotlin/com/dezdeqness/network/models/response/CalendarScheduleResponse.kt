package com.dezdeqness.network.models.response

import kotlinx.serialization.Serializable

@Serializable
data class CalendarScheduleResponse(
    val today: List<ScheduleItemResponse>,
    val tomorrow: List<ScheduleItemResponse>,
    val yesterday: List<ScheduleItemResponse>,
)
