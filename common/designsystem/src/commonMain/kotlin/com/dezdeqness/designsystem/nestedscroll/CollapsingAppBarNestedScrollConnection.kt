package com.dezdeqness.designsystem.nestedscroll

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CollapsingAppBarNestedScrollConnection(
    private val scope: CoroutineScope,
) : NestedScrollConnection {

    var headerOffset: Float by mutableFloatStateOf(0f)
        private set
    var progress: Float by mutableFloatStateOf(1f)
        private set

    var maxHeight: Float by mutableFloatStateOf(0f)
    var minHeight: Float by mutableFloatStateOf(0f)

    var isScrollEnabled: Boolean by mutableStateOf(true)

    private val animatedOffset = Animatable(0f)

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (!isScrollEnabled) return Offset.Zero

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

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        if (!isScrollEnabled) return Offset.Zero

        val delta = available.y
        val newOffset = headerOffset + delta
        val previousOffset = headerOffset
        val heightDelta = -(maxHeight - minHeight)
        headerOffset = if (heightDelta > 0) 0f else newOffset.coerceIn(heightDelta, 0f)
        progress = 1f - headerOffset / -maxHeight
        val consumedValue = headerOffset - previousOffset
        return Offset(0f, consumedValue)
    }

    private fun updateOffset(value: Float) {
        headerOffset = value
        progress = 1f - headerOffset / -maxHeight
    }

    fun collapse(animated: Boolean = true) {
        val target = -(maxHeight - minHeight)
        if (animated) animateTo(target) else updateOffset(target)
    }

    fun expand(animated: Boolean = true) {
        val target = 0f
        if (animated) animateTo(target) else updateOffset(target)
    }

    private fun animateTo(target: Float) {
        scope.launch {
            animatedOffset.snapTo(headerOffset)
            animatedOffset.animateTo(
                targetValue = target,
                animationSpec = tween(durationMillis = 400)
            ) {
                updateOffset(value)
            }
        }
    }
}

val LocalNestedScrollConnection = staticCompositionLocalOf<CollapsingAppBarNestedScrollConnection> {
    error("CollapsingAppBarNestedScrollConnection should be set")
}
