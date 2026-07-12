package com.dezdeqness.cache

sealed interface CachedResult<out T> {
    val value: T

    data class CachedValue<out T>(override val value: T) : CachedResult<T>
    data class RemoteValue<out T>(override val value: T) : CachedResult<T>
}
