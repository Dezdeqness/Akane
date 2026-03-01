package com.dezdeqness.foundation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> StateFlow<T>.collectAsStateOnLifecycle(): State<T> =
    collectAsState()

actual suspend fun Lifecycle.repeatOnResumedState(block: suspend CoroutineScope.() -> Unit) {
    coroutineScope(block)
}
