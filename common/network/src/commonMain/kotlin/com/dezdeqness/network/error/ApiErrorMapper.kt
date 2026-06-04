package com.dezdeqness.network.error

import com.dezdeqness.network.exception.ApiException

class ApiErrorMapper : ErrorMapper {

    override fun map(exception: Throwable): ErrorEntity = when (exception) {
        is ApiException -> {
            if (exception.code == HTTP_UNAUTHORIZED || exception.code == HTTP_FORBIDDEN) {
                ErrorEntity.UnauthorizedErrorEntity
            } else {
                ErrorEntity.UnknownErrorEntity(
                    "Code: ${exception.code}, message=${exception.message}",
                )
            }
        }

        else -> {
            ErrorEntity.UnknownErrorEntity(
                "Message: ${exception.message}, stack: ${exception.stackTraceToString()}",
            )
        }
    }

    private companion object {
        private const val HTTP_UNAUTHORIZED = 401
        private const val HTTP_FORBIDDEN = 403
    }
}
