package com.dezdeqness.downloads.data.platform

import java.io.File

actual class DownloadDirectoryProvider {
    actual fun getDownloadDirectory(): String {
        val userHome = System.getProperty("user.home")
        val dir = File(userHome, ".akane/downloads")
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath
    }

    actual fun getTempDirectory(): String {
        val userHome = System.getProperty("user.home")
        val dir = File(userHome, ".akane/downloads_temp")
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath
    }
}
