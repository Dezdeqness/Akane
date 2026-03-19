package com.dezdeqness.downloads.data.platform

import co.touchlab.kermit.Logger
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import kotlinx.coroutines.withContext

actual class VideoRemuxer(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    actual suspend fun remux(inputTsPath: String, outputMp4Path: String): Boolean =
        withContext(coroutineDispatcherProvider.io()) {
            try {
                val process = ProcessBuilder(
                    "ffmpeg",
                    "-i", inputTsPath,
                    "-c", "copy",
                    "-y",
                    outputMp4Path,
                ).redirectErrorStream(true).start()

                val output = process.inputStream.bufferedReader().readText()

                val exitCode = process.waitFor()
                if (exitCode == 0) {
                    Logger.d(TAG) { "Remux completed: $outputMp4Path" }
                    true
                } else {
                    Logger.e(TAG) { "ffmpeg exited with code $exitCode: $output" }
                    false
                }
            } catch (e: Exception) {
                Logger.e(TAG, e) { "ffmpeg not available or failed: ${e.message}" }
                false
            }
        }

    companion object {
        private const val TAG = "VideoRemuxer"
    }
}
