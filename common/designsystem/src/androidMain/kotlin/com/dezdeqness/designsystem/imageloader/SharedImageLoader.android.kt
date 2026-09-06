package com.dezdeqness.designsystem.imageloader

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.allowHardware
import coil3.request.crossfade
import okio.Path.Companion.toOkioPath

@Composable
actual fun getImageLoader(): ImageLoader {
    val context = LocalContext.current

    return remember {
        ImageLoader.Builder(context)
            .allowHardware(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("coil").toOkioPath())
                    .maxSizeBytes(256 * 1024 * 1024L)
                    .build()
            }
            .build()
    }
}
