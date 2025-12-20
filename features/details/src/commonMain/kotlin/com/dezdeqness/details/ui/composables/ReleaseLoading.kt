package com.dezdeqness.details.ui.composables

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.rememberShimmerOffset
import com.dezdeqness.core.ui.views.shimmer

@Composable
fun ReleaseLoading(modifier: Modifier = Modifier) {

    val shimmerOffset by rememberShimmerOffset()

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ReleaseHeaderLoading(shimmerOffset = shimmerOffset)

        HorizontalListLoading(shimmerOffset)

        HeaderLoading(shimmerOffset)

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(6) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .shimmer(shimmerOffset = shimmerOffset)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shimmer(shimmerOffset = shimmerOffset)
        )

        HeaderLoading(shimmerOffset)

        HorizontalListLoading(shimmerOffset)
    }
}

@Composable
private fun HeaderLoading(shimmerOffset: Float) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(18.dp)
            .shimmer(shimmerOffset = shimmerOffset)
    )
}

@Composable
private fun HorizontalListLoading(shimmerOffset: Float) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState(), enabled = false),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
                    .shimmer(shimmerOffset = shimmerOffset)
            )
        }
    }
}

@Composable
private fun ReleaseHeaderLoading(shimmerOffset: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2f / 3f)
                    .shimmer(shimmerOffset = shimmerOffset)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.Bottom
            ) {

                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(20.dp)
                        .shimmer(shimmerOffset = shimmerOffset)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(16.dp)
                            .shimmer(shimmerOffset = shimmerOffset)
                    )

                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .width(40.dp)
                            .height(16.dp)
                            .shimmer(shimmerOffset = shimmerOffset)
                    )
                }
            }
        }
    }
}
