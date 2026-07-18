package com.dezdeqness.downloads.notification

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DownloadNotificationController(
    private val eventBus: DownloadEventDispatcher,
    private val notifier: DownloadNotifier,
    private val coroutineScope: CoroutineScope,
) {

    private var collectJob: Job? = null
    private val lastShownPercent = mutableMapOf<Long, Int>()

    fun start() {
        if (collectJob?.isActive == true) return

        collectJob = coroutineScope.launch {
            eventBus.events.collect { event ->
                runCatching { handle(event) }
                    .onFailure { Logger.w(TAG) { "Notifier failed for $event: ${it.message}" } }
            }
        }
    }

    fun stop() {
        collectJob?.cancel()
        collectJob = null
    }

    private fun handle(event: DownloadEvent) {
        val downloadId = event.info.downloadId

        when (event) {
            is DownloadEvent.Queued -> Unit

            is DownloadEvent.Started -> {
                lastShownPercent[downloadId] = event.progress.toPercent()
                notifier.showProgress(event.info, event.progress)
            }

            is DownloadEvent.Progress -> {
                val percent = event.progress.toPercent()
                val lastPercent = lastShownPercent[downloadId]
                val shouldShow = lastPercent == null ||
                        percent - lastPercent >= PROGRESS_STEP_PERCENT ||
                        percent >= 100

                if (shouldShow) {
                    lastShownPercent[downloadId] = percent
                    notifier.showProgress(event.info, event.progress)
                }
            }

            is DownloadEvent.Completed -> {
                lastShownPercent.remove(downloadId)
                notifier.showCompleted(event.info)
            }

            is DownloadEvent.Failed -> {
                lastShownPercent.remove(downloadId)
                notifier.showFailed(event.info)
            }

            is DownloadEvent.Paused,
            is DownloadEvent.Cancelled -> {
                lastShownPercent.remove(downloadId)
                notifier.dismiss(downloadId)
            }
        }
    }

    private fun Float.toPercent(): Int = (this * 100).toInt()

    companion object {
        private const val TAG = "DownloadNotificationController"
        private const val PROGRESS_STEP_PERCENT = 5
    }
}
