package com.dezdeqness.downloads.data.manager.engine

import co.touchlab.kermit.Logger
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class)
object BookmarkPaths {

    const val PREFIX = "bookmark://"

    fun create(url: NSURL): String? {
        val data = url.bookmarkDataWithOptions(
            options = 0u,
            includingResourceValuesForKeys = null,
            relativeToURL = null,
            error = null,
        ) ?: return null

        return PREFIX + data.base64EncodedStringWithOptions(0u)
    }

    @OptIn(BetaInteropApi::class)
    fun resolve(filePath: String): NSURL? {
        if (!filePath.startsWith(PREFIX)) return null
        val base64 = filePath.removePrefix(PREFIX)
        val data = NSData.create(base64EncodedString = base64, options = 0u) ?: return null

        val url = NSURL.URLByResolvingBookmarkData(
            bookmarkData = data,
            options = 0u,
            relativeToURL = null,
            bookmarkDataIsStale = null,
            error = null,
        )

        val path = url?.path
        if (path == null || !NSFileManager.defaultManager.fileExistsAtPath(path)) {
            return null
        }
        return url
    }

    fun deleteAt(location: NSURL) {
        runCatching {
            NSFileManager.defaultManager.removeItemAtURL(location, error = null)
        }.onFailure {
            Logger.w(TAG) { "Failed to delete movpkg at $location: ${it.message}" }
        }
    }

    private const val TAG = "BookmarkPaths"
}
