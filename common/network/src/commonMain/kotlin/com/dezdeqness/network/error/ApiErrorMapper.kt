package com.dezdeqness.network.error

import com.dezdeqness.network.exception.ApiException

class ApiErrorMapper : ErrorMapper {

    override fun map(exception: Throwable): ErrorEntity = when (exception) {
        is ApiException -> {
            ErrorEntity.UnknownErrorEntity(
                "Code: ${exception.code}, message=${exception.message}",
            )
        }

        else -> {
            ErrorEntity.UnknownErrorEntity(
                "Message: ${exception.message}, stack: ${exception.stackTraceToString()}",
            )
        }
    }
}
