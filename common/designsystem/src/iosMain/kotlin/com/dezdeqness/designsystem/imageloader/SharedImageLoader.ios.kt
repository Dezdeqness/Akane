package com.dezdeqness.designsystem.imageloader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.CachePolicy
import coil3.util.DebugLogger

@Composable
actual fun getImageLoader(): ImageLoader {
    val context = PlatformContext.INSTANCE

    return remember {
        ImageLoader.Builder(context)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .logger(DebugLogger())
            .build()
    }
}
