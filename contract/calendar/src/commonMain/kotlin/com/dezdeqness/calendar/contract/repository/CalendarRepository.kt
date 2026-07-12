package com.dezdeqness.calendar.contract.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.calendar.contract.model.CalendarScheduleEntity
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getScheduleNow(): Flow<Result<CachedResult<CalendarScheduleEntity>>>
}
