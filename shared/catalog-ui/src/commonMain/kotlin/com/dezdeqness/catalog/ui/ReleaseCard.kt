package com.dezdeqness.catalog.ui

import androidx.compose.foundation.clickable
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
import com.dezdeqness.catalog.ui.model.ReleaseListUiModel
import com.dezdeqness.core.ui.views.image.AppImage

const val ReleaseCardAspectRatio = 2 / 3f

@Composable
fun ReleaseCard(
    modifier: Modifier = Modifier,
    item: ReleaseListUiModel,
    onReleaseClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ReleaseCardAspectRatio)
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                onClick = {
                    onReleaseClicked.invoke()
                }
            )
    ) {
        AppImage(
            data = item.imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
