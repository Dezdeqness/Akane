package com.dezdeqness.downloads.notification

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DownloadNotificationActions {

    sealed interface Action {
        data object AppForeground : Action
    }

    private val _actions = MutableSharedFlow<Action>(
        extraBufferCapacity = BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    fun openForeground() {
        _actions.tryEmit(Action.AppForeground)
    }

    companion object {
        private const val BUFFER_CAPACITY = 8
    }
}
