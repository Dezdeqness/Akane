package com.dezdeqness.personal.data.datasource.impl

import com.dezdeqness.personal.data.datasource.PersonalDatasource
import com.dezdeqness.personal.data.db.PersonalDao
import com.dezdeqness.personal.data.mapper.PersonalMapper
import com.dezdeqness.personal.domain.models.PersonalEntity
import kotlinx.coroutines.flow.map

class PersonalDatasourceImpl(
    private val personalDao: PersonalDao,
    private val personalMapper: PersonalMapper,
) : PersonalDatasource {
    override fun getPersonalAsFlow() =
        personalDao.getPersonalAsFlow().map {
            it.map(personalMapper::fromLocal)
        }

    override suspend fun containsById(id: Long) = personalDao.contains(id)

    override suspend fun getPersonalList() =
        personalDao.getPersonalList().map(personalMapper::fromLocal)

    override suspend fun deleteById(id: Long) {
        personalDao.delete(id)
    }

    override suspend fun add(item: PersonalEntity) {
        val localItem = personalMapper.toLocal(item)
        personalDao.insert(localItem)
    }
}
