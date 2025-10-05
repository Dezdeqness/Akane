package com.dezdeqness.personal.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.personal.ui.model.PersonalUiModel

@Composable
fun PersonalCell(
    modifier: Modifier = Modifier,
    item: PersonalUiModel,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2 / 3f)
    ) {
        AppImage(
            data = item.poster,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        )
    }

}
