package com.dezdeqness.calendar.data.datasource.impl

import com.dezdeqness.calendar.data.datasource.CalendarApiDatasource
import com.dezdeqness.calendar.data.mapper.CalendarMapper
import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.CalendarService

class CalendarApiDatasourceImpl(
    private val calendarService: CalendarService,
    private val calendarMapper: CalendarMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), CalendarApiDatasource {

    override suspend fun getScheduleNow() = tryWithCatchSuspend {
        val response = calendarService.getScheduleNow()

        if (response.isSuccessful) {
            val body = response.body()
                ?: throw response.createApiException()

            Result.success(calendarMapper.map(body))
        } else {
            throw response.createApiException()
        }
    }
}
