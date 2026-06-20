package com.dezdeqness.home.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.rememberShimmerOffset
import com.dezdeqness.core.ui.views.shimmer
import com.dezdeqness.designsystem.state.ErrorState
import com.dezdeqness.designsystem.utils.LottieFiles
import com.dezdeqness.designsystem.utils.LottieRes
import com.dezdeqness.home.ui.StateStatus
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
fun HomeSecondPartFooter(
    status: StateStatus,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (status) {
        StateStatus.LoadingMore -> HomeSecondPartShimmer(modifier = modifier)

        StateStatus.SecondPartError -> HomeSecondPartError(
            modifier = modifier,
            onRetry = onRetry,
        )

        else -> Unit
    }
}

@Composable
private fun HomeSecondPartError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.DotLottie(
            LottieRes.readBytes(LottieFiles.LottieErrorV2)
        )
    }
    val progress by animateLottieCompositionAsState(
        iterations = Compottie.IterateForever,
        composition = composition,
    )

    ErrorState(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        title = "Произошла ошибка, повторите еще раз",
        buttonTitle = "Повторить",
        onClick = onRetry,
        image = {
            Image(
                modifier = Modifier.size(96.dp),
                painter = rememberLottiePainter(
                    composition = composition,
                    progress = { progress },
                ),
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun HomeSecondPartShimmer(
    modifier: Modifier = Modifier,
    shelves: Int = 3,
) {
    val shimmerOffset by rememberShimmerOffset()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        repeat(shelves) {
            Column {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .width(100.dp)
                        .height(20.dp)
                        .shimmer(shimmerOffset = shimmerOffset, color = AppTheme.colors.surface),
                )

                LazyRow(
                    userScrollEnabled = false,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(count = 5) {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .width(100.dp)
                                .height(120.dp)
                                .shimmer(
                                    shimmerOffset = shimmerOffset,
                                    color = AppTheme.colors.surface,
                                    shape = AppTheme.shapes.medium,
                                ),
                        )
                    }
                }
            }
        }
    }
}
