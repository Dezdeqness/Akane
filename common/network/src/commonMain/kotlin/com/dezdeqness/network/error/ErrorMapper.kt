package com.dezdeqness.network.error

interface ErrorMapper {
    fun map(exception: Throwable): ErrorEntity
}
