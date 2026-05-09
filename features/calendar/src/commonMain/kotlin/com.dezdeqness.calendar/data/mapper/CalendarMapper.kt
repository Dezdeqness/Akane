package com.dezdeqness.calendar.data.mapper

import com.dezdeqness.calendar.contract.model.CalendarScheduleEntity
import com.dezdeqness.calendar.contract.model.ScheduleItemEntity
import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.network.models.response.CalendarScheduleResponse
import com.dezdeqness.network.models.response.ScheduleItemResponse

class CalendarMapper {

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
            poster = BaseUrl.BASE_URL_IMAGES + response.release.poster.src,
            type = response.release.type.value.orEmpty(),
            description = response.release.description.orEmpty(),
            fullSeasonIsReleased = response.fullSeasonIsReleased,
        )
}
