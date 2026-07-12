package com.dezdeqness.calendar.data.cache

import com.dezdeqness.calendar.contract.model.CalendarScheduleEntity
import com.dezdeqness.calendar.contract.model.ScheduleItemEntity

class CalendarCacheMapper {

    fun toSnapshot(entity: CalendarScheduleEntity) = CalendarScheduleSnapshot(
        today = entity.today.map(::itemToSnapshot),
        tomorrow = entity.tomorrow.map(::itemToSnapshot),
        yesterday = entity.yesterday.map(::itemToSnapshot),
    )

    fun toEntity(snapshot: CalendarScheduleSnapshot) = CalendarScheduleEntity(
        today = snapshot.today.map(::itemToEntity),
        tomorrow = snapshot.tomorrow.map(::itemToEntity),
        yesterday = snapshot.yesterday.map(::itemToEntity),
    )

    private fun itemToSnapshot(entity: ScheduleItemEntity) = ScheduleItemSnapshot(
        id = entity.id,
        name = entity.name,
        poster = entity.poster,
        type = entity.type,
        description = entity.description,
        fullSeasonIsReleased = entity.fullSeasonIsReleased,
    )

    private fun itemToEntity(snapshot: ScheduleItemSnapshot) = ScheduleItemEntity(
        id = snapshot.id,
        name = snapshot.name,
        poster = snapshot.poster,
        type = snapshot.type,
        description = snapshot.description,
        fullSeasonIsReleased = snapshot.fullSeasonIsReleased,
    )
}
