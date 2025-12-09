package com.dezdeqness.calendar.domain.repository

import com.dezdeqness.calendar.domain.model.CalendarScheduleEntity

interface CalendarRepository {
    suspend fun getScheduleNow(): Result<CalendarScheduleEntity>
}
