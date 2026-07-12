package com.dezdeqness.cache

import com.dezdeqness.cache.CachedResult.CachedValue
import com.dezdeqness.cache.CachedResult.RemoteValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> staleWhileRevalidate(
    read: suspend () -> T?,
    fetch: suspend () -> Result<T>,
    write: suspend (T) -> Unit,
): Flow<Result<CachedResult<T>>> = flow {
    val cached = read()
    if (cached != null) {
        emit(Result.success(CachedValue(cached)))
    }

    fetch().fold(
        onSuccess = { fresh ->
            write(fresh)
            emit(Result.success(RemoteValue(fresh)))
        },
        onFailure = { error ->
            if (cached == null) {
                emit(Result.failure(error))
            }
        },
    )
}
