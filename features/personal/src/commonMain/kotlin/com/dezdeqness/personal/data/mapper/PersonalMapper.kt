package com.dezdeqness.personal.data.mapper

import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.core.GeneralResponse
import com.dezdeqness.network.models.response.ReleaseResponse
import com.dezdeqness.personal.contract.model.PersonalEntity
import com.dezdeqness.personal.contract.model.PersonalPageEntity

class PersonalMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun fromRelease(response: ReleaseResponse) =
        PersonalEntity(
            id = response.id,
            name = response.name.main,
            poster = imageUrlBuilder.build(response.poster.src),
        )

    fun toPage(response: GeneralResponse<List<ReleaseResponse>>): PersonalPageEntity {
        val items = response.data.orEmpty().map(::fromRelease)
        val pagination = response.meta.pagination
        val currentPage = pagination.currentPage
        return PersonalPageEntity(
            items = items,
            currentPage = currentPage,
            nextPage = currentPage + 1,
            hasNextPage = currentPage < pagination.totalPages,
        )
    }
}
