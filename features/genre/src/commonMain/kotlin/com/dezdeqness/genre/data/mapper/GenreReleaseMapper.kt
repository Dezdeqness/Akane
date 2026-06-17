package com.dezdeqness.genre.data.mapper

import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.network.models.response.ReleaseResponse

class GenreReleaseMapper {

    fun map(response: ReleaseResponse) = ReleaseEntity(
        id = response.id,
        name = response.name.main,
        poster = BaseUrl.BASE_URL_IMAGES + response.poster.src,
        type = response.type.value.orEmpty(),
        description = response.description.orEmpty(),
    )

}
