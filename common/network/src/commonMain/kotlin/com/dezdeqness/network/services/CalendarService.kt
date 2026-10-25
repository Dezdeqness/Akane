package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.SCHEDULE_NOW
import com.dezdeqness.network.constants.ApiEndPoints.SCHEDULE_WEEK
import com.dezdeqness.network.models.response.CalendarScheduleResponse
import com.dezdeqness.network.models.response.ScheduleItemResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET

interface CalendarService {
    @GET(SCHEDULE_NOW)
    suspend fun getScheduleNow(): Response<CalendarScheduleResponse>

    @GET(SCHEDULE_WEEK)
    suspend fun getScheduleWeek(): Response<List<ScheduleItemResponse>>
}
