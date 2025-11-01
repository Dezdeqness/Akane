package com.dezdeqness.details.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.image.AppImage
import com.dezdeqness.details.ui.model.ReleaseDetailsHeaderUiModel

@Composable
fun ReleaseHeader(
    modifier: Modifier = Modifier,
    header: ReleaseDetailsHeaderUiModel,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        AppImage(
            data = header.imageUrl,
            modifier = Modifier
                .padding(top = 56.dp)
                .height(250.dp)
                .aspectRatio(3 / 4f)
                .clip(RoundedCornerShape(8.dp))
        )

        Text(
            header.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            if (header.season.isNotEmpty()) {
                Text(
                    header.season,
                    fontSize = 14.sp,
                    color = AppTheme.colors.textSecondary,
                )
            }
            if (header.year.isNotEmpty()) {
                Text(
                    " • ${header.year}",
                    fontSize = 14.sp,
                    color = AppTheme.colors.textSecondary,
                )
            }
        }
    }
}
