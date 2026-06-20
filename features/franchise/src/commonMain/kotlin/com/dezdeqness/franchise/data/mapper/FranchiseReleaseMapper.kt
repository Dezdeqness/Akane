package com.dezdeqness.franchise.data.mapper

import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.franchise.contract.model.FranchiseReleaseEntity
import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.response.FranchiseRelease
import com.dezdeqness.network.models.response.FranchiseReleaseItemResponse
import com.dezdeqness.network.models.response.ReleaseResponse

class FranchiseReleaseMapper(
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: FranchiseReleaseItemResponse) = FranchiseReleaseEntity(
        id = response.id,
        sortOrder = response.sortOrder.toInt(),
        releaseId = response.releaseId,
        franchiseId = response.franchiseId,
        release = mapRelease(response.release),
    )

    fun map(response: FranchiseRelease) = FranchiseReleaseEntity(
        id = response.id,
        sortOrder = response.sortOrder.toInt(),
        releaseId = response.releaseId,
        franchiseId = response.franchiseId,
        release = mapRelease(response.release),
    )

    private fun mapRelease(release: ReleaseResponse) = ReleaseEntity(
        id = release.id,
        name = release.name.main,
        poster = imageUrlBuilder.build(release.poster.src),
        type = release.type.value.orEmpty(),
        description = release.description.orEmpty(),
    )
}
