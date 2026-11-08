package com.dezdeqness.stories.ui.composables

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun StoryTapZones(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onHoldChanged: (Boolean) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        TapZone(
            modifier = Modifier.weight(SIDE_ZONE_WEIGHT).fillMaxHeight(),
            onTap = onPrevious,
            onHoldChanged = onHoldChanged,
        )
        Spacer(Modifier.weight(1f - 2 * SIDE_ZONE_WEIGHT))
        TapZone(
            modifier = Modifier.weight(SIDE_ZONE_WEIGHT).fillMaxHeight(),
            onTap = onNext,
            onHoldChanged = onHoldChanged,
        )
    }
}

@Composable
private fun TapZone(
    onTap: () -> Unit,
    onHoldChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val currentOnTap = rememberUpdatedState(onTap)
    val currentOnHold = rememberUpdatedState(onHoldChanged)

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    val holdJob = scope.launch {
                        delay(HOLD_DELAY_MS)
                        currentOnHold.value(true)
                    }
                    tryAwaitRelease()
                    holdJob.cancel()
                    currentOnHold.value(false)
                },
                onLongPress = {},
                onTap = { currentOnTap.value() },
            )
        },
    )
}

private const val SIDE_ZONE_WEIGHT = 0.3f
private const val HOLD_DELAY_MS = 200L
