package com.dezdeqness.downloads.data.platform

expect class VideoRemuxer {
    suspend fun remux(inputTsPath: String, outputMp4Path: String): Boolean
}
