package com.dezdeqness.details.ui.composables.info

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.designsystem.icons.AkaneIcons

@Composable
fun DescriptionSection(summary: String) {
    var isExpanded by remember { mutableStateOf(false) }
    var isTextTruncated by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    Header(
        title = "Описание",
        titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            summary,
            fontSize = 14.sp,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.78f),
            maxLines = if (isExpanded) Int.MAX_VALUE else 5,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                isTextTruncated = textLayoutResult.hasVisualOverflow
            }
        )

        if (isTextTruncated) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = AkaneIcons.ArrowDropDown,
                    contentDescription = null,
                    tint = AppTheme.colors.surface,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}
