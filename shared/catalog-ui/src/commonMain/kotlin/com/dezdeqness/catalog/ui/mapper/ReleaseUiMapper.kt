package com.dezdeqness.catalog.ui.mapper

import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel

class ReleaseUiMapper {

    fun map(item: ReleaseEntity) = ReleaseListUiModel(
        id = item.id,
        title = item.name,
        summary = item.description,
        imageUrl = item.poster,
    )

}
