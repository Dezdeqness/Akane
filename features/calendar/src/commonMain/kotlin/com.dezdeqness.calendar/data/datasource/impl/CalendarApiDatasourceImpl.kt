package com.dezdeqness.calendar.data.datasource.impl

import com.dezdeqness.calendar.data.datasource.CalendarApiDatasource
import com.dezdeqness.calendar.data.mapper.CalendarMapper
import com.dezdeqness.network.services.CalendarService

class CalendarApiDatasourceImpl(
    private val calendarService: CalendarService,
    private val calendarMapper: CalendarMapper,
) : CalendarApiDatasource {

    override suspend fun getScheduleNow() = tryWithCatch {
        val response = calendarService.getScheduleNow()

        if (response.isSuccessful) {
            val body = response.body()
                ?: return@tryWithCatch Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))

            Result.success(calendarMapper.map(body))
        } else {
            Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))
        }
    }

    private suspend fun <T> tryWithCatch(block: suspend () -> Result<T>) = try {
        block()
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }
}
