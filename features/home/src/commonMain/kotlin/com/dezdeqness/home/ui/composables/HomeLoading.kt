package com.dezdeqness.home.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.rememberShimmerOffset
import com.dezdeqness.core.ui.views.shimmer

@Composable
fun HomeLoading(
    modifier: Modifier = Modifier,
) {
    val shimmerOffset by rememberShimmerOffset()

    Column(
        modifier = modifier
    ) {

        ShimmerHomeHeader(
            modifier = Modifier.padding(horizontal = 16.dp),
            shimmerOffset = shimmerOffset,
        )
        ShimmerHomeItem(
            shimmerOffset = shimmerOffset,
            width = 150.dp,
            height = 180.dp,
            shape = RoundedCornerShape(28.dp),
        )

        repeat(3) { _ ->
            ShimmerHomeHeader(
                modifier = Modifier.padding(horizontal = 16.dp),
                shimmerOffset = shimmerOffset,
            )
            ShimmerHomeItem(shimmerOffset = shimmerOffset)
        }
    }
}


@Composable
private fun ShimmerHomeHeader(
    modifier: Modifier = Modifier,
    shimmerOffset: Float,
) {
    Box(
        modifier = modifier
            .padding(vertical = 16.dp)
            .width(100.dp)
            .height(20.dp)
            .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface)
    )
}

@Composable
private fun ShimmerHomeItem(
    modifier: Modifier = Modifier,
    shimmerOffset: Float,
    times: Int = 5,
    width: Dp = 100.dp,
    height: Dp = 120.dp,
    shape: Shape = AppTheme.shapes.medium,
) {
    Box(modifier = modifier) {
        LazyRow(
            userScrollEnabled = false,
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(count = times) {
                Column(
                    modifier = Modifier.padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(height)
                            .shimmer(
                                shimmerOffset = shimmerOffset,
                                color = AppTheme.colors.surface,
                                shape = shape,
                            )
                    )
                }
            }
        }
    }
}
