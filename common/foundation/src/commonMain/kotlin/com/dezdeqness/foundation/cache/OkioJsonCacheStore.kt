package com.dezdeqness.foundation.cache

import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class OkioJsonCacheStore(
    private val fileSystem: FileSystem,
    private val baseDir: Path,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : JsonCacheStore {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun <T> write(key: String, value: T, serializer: KSerializer<T>) {
        withContext(coroutineDispatcherProvider.io()) {
            fileSystem.createDirectories(baseDir)
            val data = CacheData(
                savedAtMillis = Clock.System.now().toEpochMilliseconds(),
                payload = value,
            )
            val text = json.encodeToString(CacheData.serializer(serializer), data)
            fileSystem.write(pathFor(key)) { writeUtf8(text) }
        }
    }

    override suspend fun <T> read(key: String, serializer: KSerializer<T>, ttlMillis: Long): T? =
        readInternal(key, serializer, ttlMillis)

    override suspend fun <T> read(key: String, serializer: KSerializer<T>): T? =
        readInternal(key, serializer, ttlMillis = null)

    private suspend fun <T> readInternal(
        key: String,
        serializer: KSerializer<T>,
        ttlMillis: Long?,
    ): T? =
        withContext(coroutineDispatcherProvider.io()) {
            val path = pathFor(key)
            if (!fileSystem.exists(path)) {
                return@withContext null
            }
            runCatching {
                val text = fileSystem.read(path) { readUtf8() }
                val data = json.decodeFromString(CacheData.serializer(serializer), text)
                val isFresh = ttlMillis == null || run {
                    val ageMillis = Clock.System.now().toEpochMilliseconds() - data.savedAtMillis
                    ageMillis in 0..ttlMillis
                }
                if (isFresh) data.payload else null
            }.getOrNull()
        }

    override suspend fun remove(key: String) {
        withContext(coroutineDispatcherProvider.io()) {
            val path = pathFor(key)
            if (fileSystem.exists(path)) {
                fileSystem.delete(path)
            }
        }
    }

    private fun pathFor(key: String): Path = baseDir / "$key.json"
}
