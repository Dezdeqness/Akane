package com.dezdeqness.franchise.ui.mapper

import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.franchise.ui.model.FranchiseHeaderUiModel

class FranchiseHeaderUiMapper {

    fun map(entity: FranchiseEntity) = FranchiseHeaderUiModel(
        name = entity.name,
        imageUrl = entity.imageUrl,
        meta = buildList {
            yearsLabel(entity.firstYear, entity.lastYear)?.let(::add)
            entity.totalDuration?.takeIf { it.isNotBlank() }?.let(::add)
            if (entity.totalEpisodes > 0) {
                add("${entity.totalEpisodes} эп.")
            }
        },
    )

    private fun yearsLabel(firstYear: Int, lastYear: Int): String? = when {
        firstYear <= 0 && lastYear <= 0 -> null
        firstYear <= 0 -> lastYear.toString()
        lastYear <= 0 -> firstYear.toString()
        firstYear == lastYear -> firstYear.toString()
        else -> "$firstYear–$lastYear"
    }
}
