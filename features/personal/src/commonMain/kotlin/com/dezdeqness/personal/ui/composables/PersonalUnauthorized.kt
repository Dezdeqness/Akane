package com.dezdeqness.personal.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.designsystem.state.ErrorState
import com.dezdeqness.designsystem.utils.LottieFiles
import com.dezdeqness.designsystem.utils.LottieRes
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
fun PersonalUnauthorized(
    modifier: Modifier = Modifier,
    onAction: () -> Unit,
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

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        ErrorState(
            modifier = Modifier.padding(16.dp),
            title = "Войдите в аккаунт, чтобы увидеть избранное",
            buttonTitle = "Перейти в профиль",
            onClick = onAction,
            image = {
                Image(
                    modifier = Modifier.size(120.dp).offset(y = 24.dp),
                    painter = rememberLottiePainter(
                        composition = composition,
                        progress = { progress },
                    ),
                    contentDescription = null,
                )
            }
        )
    }
}
