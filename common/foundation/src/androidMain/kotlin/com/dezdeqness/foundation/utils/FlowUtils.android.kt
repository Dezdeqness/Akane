package com.dezdeqness.foundation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> StateFlow<T>.collectAsStateOnLifecycle(): State<T> =
    collectAsStateWithLifecycle()

actual suspend fun Lifecycle.repeatOnResumedState(block: suspend CoroutineScope.() -> Unit) {
    repeatOnLifecycle(Lifecycle.State.RESUMED, block)
}
