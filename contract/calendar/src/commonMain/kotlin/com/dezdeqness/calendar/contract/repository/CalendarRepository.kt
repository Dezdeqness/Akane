package com.dezdeqness.calendar.contract.repository

import com.dezdeqness.calendar.contract.model.CalendarScheduleEntity

interface CalendarRepository {
    suspend fun getScheduleNow(): Result<CalendarScheduleEntity>
}
