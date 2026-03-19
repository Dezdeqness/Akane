package com.dezdeqness.downloads.data.platform

import android.content.Context
import java.io.File

actual class DownloadDirectoryProvider(private val context: Context) {
    actual fun getDownloadDirectory(): String {
        val dir = File(context.filesDir, "downloads")
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath
    }

    actual fun getTempDirectory(): String {
        val dir = File(context.filesDir, "downloads_temp")
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath
    }
}
