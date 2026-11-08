package com.dezdeqness.stories.ui.composables

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import com.dezdeqness.stories.ui.model.StoryUiModel

internal data class IndexedStory(val index: Int, val story: StoryUiModel)

internal fun AnimatedContentTransitionScope<IndexedStory>.storySwapTransition(): ContentTransform {
    val direction = if (targetState.index >= initialState.index) 1 else -1
    val slide = tween<IntOffset>(SWAP_DURATION_MS, easing = FastOutSlowInEasing)
    return (slideInHorizontally(slide) { width -> direction * width / SWAP_SHIFT_DIVIDER } +
        fadeIn(tween(SWAP_DURATION_MS, easing = LinearOutSlowInEasing))) togetherWith
        (slideOutHorizontally(slide) { width -> -direction * width / SWAP_SHIFT_DIVIDER } +
            fadeOut(tween(SWAP_DURATION_MS / 2, easing = FastOutLinearInEasing))) using
        SizeTransform(clip = false)
}

internal const val SWAP_DURATION_MS = 260
private const val SWAP_SHIFT_DIVIDER = 4
