package com.dezdeqness.foundation.cache

import kotlinx.serialization.KSerializer

interface JsonCacheStore {
    suspend fun <T> read(key: String, serializer: KSerializer<T>, ttlMillis: Long): T?
    suspend fun <T> read(key: String, serializer: KSerializer<T>): T?
    suspend fun <T> write(key: String, value: T, serializer: KSerializer<T>)
    suspend fun remove(key: String)
}
