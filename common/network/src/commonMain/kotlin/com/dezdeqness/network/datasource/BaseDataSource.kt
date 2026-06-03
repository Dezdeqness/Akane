package com.dezdeqness.network.datasource

import com.dezdeqness.network.error.ErrorMapper

abstract class BaseDataSource(
    private val errorMapper: ErrorMapper,
) {

    fun <T> tryWithCatch(block: () -> Result<T>): Result<T> = try {
        block()
    } catch (exception: Throwable) {
        Result.failure(errorMapper.map(exception))
    }

    suspend fun <T> tryWithCatchSuspend(block: suspend () -> Result<T>): Result<T> = try {
        block()
    } catch (exception: Throwable) {
        Result.failure(errorMapper.map(exception))
    }
}
