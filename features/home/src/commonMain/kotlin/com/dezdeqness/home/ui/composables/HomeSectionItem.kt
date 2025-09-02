package com.dezdeqness.home.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.util.DebugLogger
import com.dezdeqness.home.ui.model.HomeUiModel

@Composable
fun HomeSectionItem(
    modifier: Modifier = Modifier,
    item: HomeUiModel,
    onItemClicked: (Long) -> Unit,
) {
    val context = LocalPlatformContext.current
    val loader = remember {
        ImageLoader.Builder(context)
            .logger(DebugLogger())
            .build()
    }

    Column(
        modifier = modifier
            .width(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = {
                    onItemClicked.invoke(item.id)
                },
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AsyncImage(
            model = remember(item.imagePath) {
                ImageRequest.Builder(context)
                    .data(item.imagePath)
                    .build()
            },
            contentDescription = null,
            imageLoader = loader,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .width(140.dp)
                .clip(RoundedCornerShape(12.dp)),
        )

        Text(
            text = item.name,
            modifier = Modifier.padding(horizontal = 2.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
