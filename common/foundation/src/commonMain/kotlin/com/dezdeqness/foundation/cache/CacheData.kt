package com.dezdeqness.foundation.cache

import kotlinx.serialization.Serializable

@Serializable
data class CacheData<T>(
    val savedAtMillis: Long,
    val payload: T,
)
