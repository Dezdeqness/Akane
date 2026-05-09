package com.dezdeqness.personal.ui.mapper

import com.dezdeqness.personal.contract.model.PersonalEntity
import com.dezdeqness.personal.ui.model.PersonalUiModel

class PersonalUiMapper {
    fun toUiModel(item: PersonalEntity) =
        PersonalUiModel(
            id = item.id,
            name = item.name,
            poster = item.poster,
        )
}
