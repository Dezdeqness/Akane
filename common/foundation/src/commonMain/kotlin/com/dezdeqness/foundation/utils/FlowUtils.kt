package com.dezdeqness.foundation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> Flow<T>.collectEvents(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    sideEffect: (suspend (event: T) -> Unit),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(this@collectEvents, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            collect {
                sideEffect(it)
            }
        }
    }
}

@Composable
expect fun <T> StateFlow<T>.collectAsStateOnLifecycle(): State<T>

expect suspend fun Lifecycle.repeatOnResumedState(block: suspend kotlinx.coroutines.CoroutineScope.() -> Unit)
