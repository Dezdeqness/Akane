package com.dezdeqness.stories.ui.composables

import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs

internal fun Modifier.cubeTransition(pagerState: PagerState, page: Int) = graphicsLayer {
    val offset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
    cameraDistance = 16f * density
    transformOrigin = TransformOrigin(pivotFractionX = if (offset < 0f) 0f else 1f, pivotFractionY = 0.5f)
    rotationY = -90f * offset.coerceIn(-1f, 1f)
    clip = true
    alpha = if (abs(offset) < 1f) 1f else 0f
}
