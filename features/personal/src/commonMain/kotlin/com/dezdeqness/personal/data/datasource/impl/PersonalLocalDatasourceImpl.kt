package com.dezdeqness.personal.data.datasource.impl

import com.dezdeqness.personal.contract.model.PersonalEntity
import com.dezdeqness.personal.data.datasource.PersonalLocalDatasource
import com.dezdeqness.personal.data.db.PersonalDao
import com.dezdeqness.personal.data.mapper.PersonalMapper
import kotlinx.coroutines.flow.map

class PersonalLocalDatasourceImpl(
    private val personalDao: PersonalDao,
    private val personalMapper: PersonalMapper,
) : PersonalLocalDatasource {
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
