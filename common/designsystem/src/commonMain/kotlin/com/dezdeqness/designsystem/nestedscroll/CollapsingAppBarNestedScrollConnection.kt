package com.dezdeqness.designsystem.nestedscroll

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

class CollapsingAppBarNestedScrollConnection : NestedScrollConnection {

    var headerOffset: Float by mutableFloatStateOf(0f)
        private set
    var progress: Float by mutableFloatStateOf(1f)
        private set

    var maxHeight: Float by mutableFloatStateOf(0f)
    var minHeight: Float by mutableFloatStateOf(0f)

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y

        if (delta >= 0f) {
            return Offset.Zero
        }
        val newOffset = headerOffset + delta
        val previousOffset = headerOffset
        val heightDelta = -(maxHeight - minHeight)
        headerOffset = if (heightDelta > 0) 0f else newOffset.coerceIn(heightDelta, 0f)
        progress = 1f - headerOffset / -maxHeight
        val consumed = headerOffset - previousOffset
        return Offset(0f, consumed)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        val delta = available.y
        val newOffset = headerOffset + delta
        val previousOffset = headerOffset
        val heightDelta = -(maxHeight - minHeight)
        headerOffset = if (heightDelta > 0) 0f else newOffset.coerceIn(heightDelta, 0f)
        progress = 1f - headerOffset / -maxHeight
        val consumedValue = headerOffset - previousOffset
        return Offset(0f, consumedValue)
    }
}

val LocalNestedScrollConnection = staticCompositionLocalOf<CollapsingAppBarNestedScrollConnection> {
    error("CollapsingAppBarNestedScrollConnection should be set")
}
