package com.dezdeqness.network.error

sealed class ErrorEntity(message: String) : Throwable(message) {

    data class UnknownErrorEntity(val errorMessage: String) : ErrorEntity(errorMessage)
}
