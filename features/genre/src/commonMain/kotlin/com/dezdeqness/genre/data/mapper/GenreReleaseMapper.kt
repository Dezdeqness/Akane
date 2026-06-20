package com.dezdeqness.genre.data.mapper

import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.response.ReleaseResponse

class GenreReleaseMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: ReleaseResponse) = ReleaseEntity(
        id = response.id,
        name = response.name.main,
        poster = imageUrlBuilder.build(response.poster.src),
        type = response.type.value.orEmpty(),
        description = response.description.orEmpty(),
    )

}
