package com.dezdeqness.stories.data.datasource.impl

import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.services.CalendarService
import com.dezdeqness.stories.data.datasource.StoriesApiDatasource
import com.dezdeqness.stories.data.mapper.StoriesMapper

class StoriesApiDatasourceImpl(
    private val calendarService: CalendarService,
    private val storiesMapper: StoriesMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), StoriesApiDatasource {

    override suspend fun getWeekStories(todayDayOfWeek: Int) = tryWithCatchSuspend {
        val response = calendarService.getScheduleWeek()

        if (response.isSuccessful) {
            val body = response.body()
                ?: throw response.createApiException()

            Result.success(storiesMapper.map(body, todayDayOfWeek))
        } else {
            throw response.createApiException()
        }
    }
}
