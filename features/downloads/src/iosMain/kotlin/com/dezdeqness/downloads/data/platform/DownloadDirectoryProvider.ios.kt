package com.dezdeqness.downloads.data.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DownloadDirectoryProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual fun getDownloadDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val dir = requireNotNull(documentDirectory?.path) + "/downloads"
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(dir)) {
            fileManager.createDirectoryAtPath(dir, withIntermediateDirectories = true, attributes = null, error = null)
        }
        return dir
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun getTempDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val dir = requireNotNull(documentDirectory?.path) + "/downloads_temp"
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(dir)) {
            fileManager.createDirectoryAtPath(dir, withIntermediateDirectories = true, attributes = null, error = null)
        }
        return dir
    }
}
