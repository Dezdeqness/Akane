package com.dezdeqness.calendar.data.mapper

import com.dezdeqness.calendar.contract.model.CalendarScheduleEntity
import com.dezdeqness.calendar.contract.model.ScheduleItemEntity
import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.response.CalendarScheduleResponse
import com.dezdeqness.network.models.response.ScheduleItemResponse

class CalendarMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: CalendarScheduleResponse) =
        CalendarScheduleEntity(
            today = response.today.map(::mapScheduleItem),
            tomorrow = response.tomorrow.map(::mapScheduleItem),
            yesterday = response.yesterday.map(::mapScheduleItem),
        )

    private fun mapScheduleItem(response: ScheduleItemResponse) =
        ScheduleItemEntity(
            id = response.release.id,
            name = response.release.name.main,
            poster = imageUrlBuilder.build(response.release.poster.src),
            type = response.release.type.value.orEmpty(),
            description = response.release.description.orEmpty(),
            fullSeasonIsReleased = response.fullSeasonIsReleased,
        )
}
