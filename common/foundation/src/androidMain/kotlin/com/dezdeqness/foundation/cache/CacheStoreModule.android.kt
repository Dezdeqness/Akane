package com.dezdeqness.foundation.cache

import android.content.Context
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual fun cacheStoreModule(): Module = module {
    single<JsonCacheStore> {
        val context = get<Context>().applicationContext
        val dir = File(context.filesDir, JSON_CACHE_DIR)
        OkioJsonCacheStore(
            fileSystem = FileSystem.SYSTEM,
            baseDir = dir.absolutePath.toPath(),
            coroutineDispatcherProvider = get(),
        )
    }
}

private const val JSON_CACHE_DIR = "json_cache"
