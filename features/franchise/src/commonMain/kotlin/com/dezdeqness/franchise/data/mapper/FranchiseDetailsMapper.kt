package com.dezdeqness.franchise.data.mapper

import com.dezdeqness.franchise.contract.model.FranchiseDetailsEntity
import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.network.constants.ImageUrlBuilder
import com.dezdeqness.network.models.response.FranchiseDetailResponse
import com.dezdeqness.network.models.response.FranchiseResponse

class FranchiseDetailsMapper(
    private val franchiseReleaseMapper: FranchiseReleaseMapper,
    private val imageUrlBuilder: ImageUrlBuilder,
) {

    fun map(response: FranchiseDetailResponse) = FranchiseDetailsEntity(
        franchise = FranchiseEntity(
            id = response.id,
            name = response.name,
            nameEnglish = response.nameEnglish.orEmpty(),
            imageUrl = imageUrlBuilder.build(response.image.optimized.preview ?: response.image.preview),
            rating = response.rating ?: 0.0,
            firstYear = response.firstYear?.toInt() ?: 0,
            lastYear = response.lastYear?.toInt() ?: 0,
            totalReleases = response.totalReleases?.toInt() ?: 0,
            totalEpisodes = response.totalEpisodes?.toInt() ?: 0,
            totalDuration = response.totalDuration,
            totalDurationInSeconds = response.totalDurationInSeconds?.toInt() ?: 0,
        ),
        releases = response.franchiseReleases
            .sortedBy { it.sortOrder }
            .map(franchiseReleaseMapper::map),
    )

    fun map(response: FranchiseResponse) = FranchiseDetailsEntity(
        franchise = FranchiseEntity(
            id = response.id,
            name = response.name,
            nameEnglish = response.nameEnglish,
            imageUrl = imageUrlBuilder.build(response.image.optimized.preview ?: response.image.preview),
            rating = 0.0,
            firstYear = response.firstYear.toInt(),
            lastYear = response.lastYear.toInt(),
            totalReleases = response.totalReleases.toInt(),
            totalEpisodes = response.totalEpisodes.toInt(),
            totalDuration = response.totalDuration,
            totalDurationInSeconds = response.totalDurationInSeconds.toInt(),
        ),
        releases = response.franchiseReleases
            .sortedBy { it.sortOrder }
            .map(franchiseReleaseMapper::map),
    )
}
