package com.dezdeqness.personal.data.datasource.impl

import com.dezdeqness.personal.data.datasource.PersonalLocalDatasource
import com.dezdeqness.personal.data.db.PersonalDao
import com.dezdeqness.personal.data.models.PersonalLocal

class PersonalLocalDatasourceImpl(
    private val personalDao: PersonalDao,
) : PersonalLocalDatasource {

    override fun getFavoriteIdsAsFlow() = personalDao.getIdsAsFlow()

    override suspend fun contains(id: Long) = personalDao.contains(id)

    override suspend fun replaceAll(ids: List<Long>) {
        personalDao.replaceAll(ids.map(::PersonalLocal))
    }

    override suspend fun add(id: Long) {
        personalDao.insert(PersonalLocal(id))
    }

    override suspend fun delete(id: Long) {
        personalDao.delete(id)
    }
}
