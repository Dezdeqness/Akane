package com.dezdeqness.downloads.data.platform

import co.touchlab.kermit.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAssetExportPresetPassthrough
import platform.AVFoundation.AVAssetExportSession
import platform.AVFoundation.AVAssetExportSessionStatusCompleted
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.AVFileTypeMPEG4
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import kotlin.coroutines.resume

actual class VideoRemuxer {

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun remux(inputTsPath: String, outputMp4Path: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            val inputUrl = NSURL.fileURLWithPath(inputTsPath)
            val outputUrl = NSURL.fileURLWithPath(outputMp4Path)

            val asset = AVURLAsset(uRL = inputUrl, options = null)
            val exportSession = AVAssetExportSession(
                asset = asset,
                presetName = AVAssetExportPresetPassthrough,
            )

            exportSession.outputURL = outputUrl
            exportSession.outputFileType = AVFileTypeMPEG4

            NSFileManager.defaultManager.removeItemAtPath(outputMp4Path, error = null)

            exportSession.exportAsynchronouslyWithCompletionHandler {
                val success = exportSession.status == AVAssetExportSessionStatusCompleted
                if (success) {
                    Logger.d(TAG) { "Remux completed: $outputMp4Path" }
                } else {
                    Logger.e(TAG) { "Remux failed: status=${exportSession.status}, error=${exportSession.error}" }
                }
                if (continuation.isActive) continuation.resume(success)
            }

            continuation.invokeOnCancellation {
                exportSession.cancelExport()
            }
        }

    companion object {
        private const val TAG = "VideoRemuxer"
    }
}
