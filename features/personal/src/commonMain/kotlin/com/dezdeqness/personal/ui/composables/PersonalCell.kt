package com.dezdeqness.personal.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.personal.ui.model.PersonalUiModel

@Composable
fun PersonalCell(
    modifier: Modifier = Modifier,
    item: PersonalUiModel,
    onRemoveItemClicked: (Long) -> Unit,
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
    ) {
        AppImage(
            data = item.poster,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(150.dp)
                .aspectRatio(2 / 3f)
                .clip(RoundedCornerShape(8.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, top = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.Bottom) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 24.dp) {
                    AppIconButton(
                        icon = AkaneIcons.Favorite,
                        onClick = {
                            onRemoveItemClicked(item.id)
                        },
                    )
                }
            }
        }
    }

}
