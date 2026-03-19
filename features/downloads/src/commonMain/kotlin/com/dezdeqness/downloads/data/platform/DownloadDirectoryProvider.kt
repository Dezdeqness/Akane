package com.dezdeqness.downloads.data.platform

expect class DownloadDirectoryProvider {
    fun getDownloadDirectory(): String
    fun getTempDirectory(): String
}
