package com.dezdeqness.genre.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.rememberShimmerOffset
import com.dezdeqness.core.ui.views.shimmer

@Composable
fun GenreLoading(
    modifier: Modifier = Modifier,
    columns: Int,
    rows: Int = 4,
    horizontalPadding: Dp = 16.dp,
) {
    val shimmerOffset by rememberShimmerOffset()

    Column(
        modifier = modifier
            .padding(horizontal = horizontalPadding, vertical = 16.dp)
            .verticalScroll(
                state = rememberScrollState(),
                enabled = false,
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(columns) {
                    GenreCardLoading(
                        modifier = Modifier.weight(1f),
                        shimmerOffset = shimmerOffset,
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreCardLoading(
    modifier: Modifier = Modifier,
    shimmerOffset: Float,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2 / 3f)
                .clip(RoundedCornerShape(12.dp))
                .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface),
        )
    }
}
