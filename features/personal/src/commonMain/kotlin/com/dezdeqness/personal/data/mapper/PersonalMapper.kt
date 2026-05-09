package com.dezdeqness.personal.data.mapper

import com.dezdeqness.personal.core.currentTimeMillis
import com.dezdeqness.personal.data.models.PersonalLocal
import com.dezdeqness.personal.contract.model.PersonalEntity

class PersonalMapper {
    fun fromLocal(item: PersonalLocal) =
        PersonalEntity(
            id = item.id,
            name = item.name,
            poster = item.poster,
        )

    fun toLocal(item: PersonalEntity) =
        PersonalLocal(
            id = item.id,
            name = item.name,
            poster = item.poster,
            createdTimeStamp = currentTimeMillis(),
        )
}
