package com.dezdeqness.calendar.data.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.cache.staleWhileRevalidate
import com.dezdeqness.calendar.contract.model.CalendarScheduleEntity
import com.dezdeqness.calendar.contract.repository.CalendarRepository
import com.dezdeqness.calendar.data.cache.CalendarCacheMapper
import com.dezdeqness.calendar.data.cache.CalendarScheduleSnapshot
import com.dezdeqness.calendar.data.datasource.CalendarApiDatasource
import com.dezdeqness.foundation.cache.JsonCacheStore
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.hours

class CalendarRepositoryImpl(
    private val calendarApiDatasource: CalendarApiDatasource,
    private val jsonCacheStore: JsonCacheStore,
    private val calendarCacheMapper: CalendarCacheMapper,
) : CalendarRepository {

    override fun getScheduleNow(): Flow<Result<CachedResult<CalendarScheduleEntity>>> =
        staleWhileRevalidate(
            read = {
                jsonCacheStore
                    .read(CACHE_KEY, SNAPSHOT_SERIALIZER, TTL_MILLIS)
                    ?.let(calendarCacheMapper::toEntity)
            },
            fetch = { calendarApiDatasource.getScheduleNow() },
            write = { jsonCacheStore.write(CACHE_KEY, calendarCacheMapper.toSnapshot(it), SNAPSHOT_SERIALIZER) },
        )

    private companion object {
        const val CACHE_KEY = "calendar_schedule_now"
        val TTL_MILLIS = 24.hours.inWholeMilliseconds
        val SNAPSHOT_SERIALIZER = CalendarScheduleSnapshot.serializer()
    }
}
