package com.dezdeqness.downloads.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.designsystem.state.EmptyState
import com.dezdeqness.designsystem.utils.LottieFiles
import com.dezdeqness.designsystem.utils.LottieRes
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
internal fun LibraryEmptyState(
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.DotLottie(
            LottieRes.readBytes(LottieFiles.LottieEmptyV1)
        )
    }
    val progress by animateLottieCompositionAsState(
        iterations = Compottie.IterateForever,
        composition = composition,
    )

    EmptyState(
        modifier = modifier.padding(16.dp),
        title = "Нет скачанных аниме",
        image = {
            Image(
                modifier = Modifier.size(120.dp).offset(y = 24.dp),
                painter = rememberLottiePainter(
                    composition = composition,
                    progress = { progress },
                ),
                contentDescription = null,
            )
        },
    )
}
