package com.dezdeqness.designsystem.imageloader

import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import okio.Path.Companion.toPath

@androidx.compose.runtime.Composable
actual fun getImageLoader(): ImageLoader {
    val context = PlatformContext.INSTANCE

    return remember {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizeBytes(128 * 1024 * 1024L)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDirectory())
                    .maxSizeBytes(256 * 1024 * 1024L)
                    .build()
            }
            .build()
    }
}

private fun cacheDirectory(): okio.Path {
    val base = System.getProperty("user.home") ?: System.getProperty("java.io.tmpdir") ?: "."
    return base.toPath() / ".akane" / "image_cache"
}
