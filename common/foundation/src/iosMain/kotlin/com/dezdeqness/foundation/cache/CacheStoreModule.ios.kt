package com.dezdeqness.foundation.cache

import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun cacheStoreModule(): Module = module {
    single<JsonCacheStore> {
        OkioJsonCacheStore(
            fileSystem = FileSystem.SYSTEM,
            baseDir = (documentDirectory() + "/" + JSON_CACHE_DIR).toPath(),
            coroutineDispatcherProvider = get(),
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

private const val JSON_CACHE_DIR = "json_cache"
