package com.dezdeqness.network.exception

import de.jensklingenberg.ktorfit.Response

class ApiException(
    val code: Int,
    val errorBody: String,
) : Throwable("Code: $code, error: $errorBody")

fun <T> Response<T>.createApiException() =
    ApiException(
        code = code,
        errorBody = errorBody()?.toString() ?: "No mappable error",
    )
