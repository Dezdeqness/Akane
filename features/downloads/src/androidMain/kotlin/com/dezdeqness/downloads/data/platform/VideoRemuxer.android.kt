package com.dezdeqness.downloads.data.platform

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import android.os.Handler
import android.os.Looper
import co.touchlab.kermit.Logger
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class VideoRemuxer(private val context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())

    @OptIn(UnstableApi::class)
    actual suspend fun remux(inputTsPath: String, outputMp4Path: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            mainHandler.post {
                val transformer = Transformer.Builder(context)
                    .addListener(object : Transformer.Listener {
                        override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                            Logger.d(TAG) { "Remux completed: $outputMp4Path" }
                            if (continuation.isActive) continuation.resume(true)
                        }

                        override fun onError(
                            composition: Composition,
                            exportResult: ExportResult,
                            exportException: ExportException,
                        ) {
                            Logger.e(TAG, exportException) { "Remux failed: ${exportException.message}" }
                            if (continuation.isActive) continuation.resume(false)
                        }
                    })
                    .build()

                val mediaItem = MediaItem.fromUri(inputTsPath)
                transformer.start(mediaItem, outputMp4Path)

                continuation.invokeOnCancellation {
                    mainHandler.post { transformer.cancel() }
                }
            }
        }

    companion object {
        private const val TAG = "VideoRemuxer"
    }
}
