package com.dezdeqness.downloads.notification

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DownloadEventDispatcher {

    private val _events = MutableSharedFlow<DownloadEvent>(
        extraBufferCapacity = BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val events: SharedFlow<DownloadEvent> = _events.asSharedFlow()

    fun emit(event: DownloadEvent) {
        _events.tryEmit(event)
    }

    companion object {
        private const val BUFFER_CAPACITY = 64
    }
}
