package com.dezdeqness.franchise.data.cache

import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.franchise.data.db.FranchiseCacheDao
import com.dezdeqness.franchise.data.db.FranchiseLocal
import com.dezdeqness.franchise.data.mapper.FranchiseMapper
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FranchiseCache(
    private val franchiseCacheDao: FranchiseCacheDao,
    private val franchiseMapper: FranchiseMapper,
) {

    private val mutex = Mutex()

    suspend fun putAll(items: List<FranchiseEntity>) = mutex.withLock {
        franchiseCacheDao.insertAll(items.map(franchiseMapper::toLocal))
    }

    suspend fun get(franchiseId: String): FranchiseEntity? =
        franchiseCacheDao.getById(franchiseId)?.let(franchiseMapper::toEntity)

    suspend fun clear() = mutex.withLock {
        franchiseCacheDao.clearAll()
    }

}
