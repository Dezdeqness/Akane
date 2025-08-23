package com.dezdeqness.personal.data.repository

import com.dezdeqness.personal.data.datasource.PersonalDatasource
import com.dezdeqness.personal.domain.models.PersonalEntity
import com.dezdeqness.personal.domain.repository.PersonalRepository

class PersonalRepositoryImpl(
    private val personalDatasource: PersonalDatasource
) : PersonalRepository {
    override fun getPersonalAsFlow() = personalDatasource.getPersonalAsFlow()

    override suspend fun containsById(id: Long) = personalDatasource.containsById(id)

    override suspend fun getPersonalList() = personalDatasource.getPersonalList()

    override suspend fun deleteById(id: Long) {
        personalDatasource.deleteById(id)
    }

    override suspend fun add(item: PersonalEntity) {
        personalDatasource.add(item)
    }
}
