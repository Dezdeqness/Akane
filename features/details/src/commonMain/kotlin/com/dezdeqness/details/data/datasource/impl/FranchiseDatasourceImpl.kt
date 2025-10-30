package com.dezdeqness.details.data.datasource.impl

import com.dezdeqness.details.data.datasource.FranchiseDatasource
import com.dezdeqness.details.data.mapper.FranchiseMapper
import com.dezdeqness.network.services.FranchiseService

class FranchiseDatasourceImpl(
    private val franchiseService: FranchiseService,
    private val franchiseMapper: FranchiseMapper,
) : FranchiseDatasource {

    override suspend fun getReleaseFranchiseById(id: Long) = tryWithCatch {
        val response = franchiseService.getReleaseFranchiseById(id = id)

        if (response.isSuccessful) {
            val body = response.body()
                ?: return@tryWithCatch Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))

            val data = body.first()

            val entity = franchiseMapper.map(data)

            Result.success(entity)
        } else {
            // TODO: custom APIException
            Result.failure(Throwable("Code: ${response.code}\nError: ${response.errorBody()}"))
        }
    }

    private suspend fun <T> tryWithCatch(block: suspend () -> Result<T>) = try {
        block()
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }

}
