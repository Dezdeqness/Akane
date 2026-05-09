package com.dezdeqness.calendar.data.repository

import com.dezdeqness.calendar.data.datasource.CalendarApiDatasource
import com.dezdeqness.calendar.contract.repository.CalendarRepository

class CalendarRepositoryImpl(
    private val calendarApiDatasource: CalendarApiDatasource,
) : CalendarRepository {

    override suspend fun getScheduleNow() = calendarApiDatasource.getScheduleNow()
}
