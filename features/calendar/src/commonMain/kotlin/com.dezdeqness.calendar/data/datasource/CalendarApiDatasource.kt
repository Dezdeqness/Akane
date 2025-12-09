package com.dezdeqness.calendar.data.datasource

import com.dezdeqness.calendar.domain.model.CalendarScheduleEntity

interface CalendarApiDatasource {
    suspend fun getScheduleNow(): Result<CalendarScheduleEntity>
}
