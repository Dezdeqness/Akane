package com.dezdeqness.franchise.ui.mapper

import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.franchise.ui.model.FranchiseUiModel

class FranchiseUiMapper {

    fun map(entity: FranchiseEntity) = FranchiseUiModel(
        id = entity.id,
        name = entity.name,
        imageUrl = entity.imageUrl,
        totalReleases = entity.totalReleases,
    )
}
