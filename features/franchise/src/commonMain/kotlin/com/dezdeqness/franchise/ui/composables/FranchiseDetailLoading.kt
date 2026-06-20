package com.dezdeqness.franchise.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.catalog.ui.ReleaseListLoading
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.rememberShimmerOffset
import com.dezdeqness.core.ui.views.shimmer

@Composable
fun FranchiseDetailLoading(
    columns: Int,
    modifier: Modifier = Modifier,
) {
    val shimmerOffset by rememberShimmerOffset()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(2f / 3f)
                        .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface),
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(20.dp)
                            .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface),
                    )
                }
            }
        }

        ReleaseListLoading(
            modifier = Modifier.weight(1f),
            gridElementCount = columns,
        )
    }
}
