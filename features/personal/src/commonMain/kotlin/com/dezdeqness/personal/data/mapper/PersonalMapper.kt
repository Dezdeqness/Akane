package com.dezdeqness.personal.data.mapper

import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.network.models.response.ReleaseResponse
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

    fun fromRelease(response: ReleaseResponse) =
        PersonalEntity(
            id = response.id,
            name = response.name.main,
            poster = BaseUrl.BASE_URL_IMAGES + response.poster.src,
        )

    fun toLocal(item: PersonalEntity) =
        PersonalLocal(
            id = item.id,
            name = item.name,
            poster = item.poster,
            createdTimeStamp = currentTimeMillis(),
        )
}
