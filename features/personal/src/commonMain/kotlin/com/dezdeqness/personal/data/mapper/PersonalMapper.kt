package com.dezdeqness.personal.data.mapper

import com.dezdeqness.personal.data.models.PersonalLocal
import com.dezdeqness.personal.domain.models.PersonalEntity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PersonalMapper {
    fun fromLocal(item: PersonalLocal) =
        PersonalEntity(
            id = item.id,
            name = item.name,
            poster = item.poster,
        )

    @OptIn(ExperimentalTime::class)
    fun toLocal(item: PersonalEntity) =
        PersonalLocal(
            id = item.id,
            name = item.name,
            poster = item.poster,
            createdTimeStamp = Clock.System.now().toEpochMilliseconds(),
        )
}
