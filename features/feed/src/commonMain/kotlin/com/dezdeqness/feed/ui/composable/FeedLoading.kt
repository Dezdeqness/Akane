package com.dezdeqness.feed.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.rememberShimmerOffset
import com.dezdeqness.core.ui.views.shimmer

@Composable
fun FeedLoading(
    modifier: Modifier = Modifier,
    times: Int = 5,
    gridElementCount: Int = 3,
) {
    val shimmerOffset by rememberShimmerOffset()

    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .verticalScroll(
                state = rememberScrollState(),
                enabled = false,
            )
    ) {
        repeat(times) { _ ->
            FeedPairLoading(shimmerOffset = shimmerOffset, gridElementCount = gridElementCount)
        }
    }
}

@Composable
fun FeedItemLoading(
    modifier: Modifier = Modifier,
    shimmerOffset: Float,
) {
    Box(
        modifier = modifier
            .aspectRatio(FeedItemAspectRatio)
            .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface)
    )
}

@Composable
private fun FeedPairLoading(
    modifier: Modifier = Modifier,
    shimmerOffset: Float,
    gridElementCount: Int,
) {
    Row(
        modifier = modifier.padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(gridElementCount) {
            FeedItemLoading(
                modifier = Modifier.weight(1f),
                shimmerOffset = shimmerOffset,
            )

        }
    }
}
