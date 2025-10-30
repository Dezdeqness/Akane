package com.dezdeqness.details.data.mapper

import com.dezdeqness.details.domain.model.FranchiseEntity
import com.dezdeqness.details.domain.model.FranchiseReleaseEntity
import com.dezdeqness.network.models.response.FranchiseRelease
import com.dezdeqness.network.models.response.FranchiseResponse

class FranchiseMapper(
    private val releaseMapper: ReleaseMapper,
) {

    fun map(response: FranchiseResponse) =
        FranchiseEntity(
            id = response.id,
            name = response.name,
            nameEnglish = response.nameEnglish,
            firstYear = response.firstYear,
            lastYear = response.lastYear,
            totalEpisodes = response.totalEpisodes,
            totalReleases = response.totalReleases,
            totalDuration = response.totalDuration,
            totalDurationInSeconds = response.totalDurationInSeconds,
            franchiseReleases = response.franchiseReleases.map(::mapFranchiseRelease),
        )

    private fun mapFranchiseRelease(release: FranchiseRelease) =
        FranchiseReleaseEntity(
            id = release.id,
            sortOrder = release.sortOrder,
            releaseId = release.releaseId,
            franchiseId = release.franchiseId,
            release = releaseMapper.map(release.release),
        )

}
