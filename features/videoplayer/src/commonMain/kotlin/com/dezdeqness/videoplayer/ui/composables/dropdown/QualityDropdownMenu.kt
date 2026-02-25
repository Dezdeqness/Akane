package com.dezdeqness.videoplayer.ui.composables.dropdown

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.details.domain.model.VideoQuality

@Composable
fun QualityDropdownMenu(
    isExpanded: Boolean,
    currentQuality: VideoQuality,
    onQualityChange: (VideoQuality) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { onDismiss() }
    ) {
        VideoQuality.entries.toTypedArray().forEach { item ->
            DropdownMenuItem(
                text = { Text(item.nameQuality, color = Color.Black) },
                onClick = {
                    onQualityChange(item)
                    onDismiss()
                },
                trailingIcon = {
                    if (item == currentQuality) Icon(AkaneIcons.Check, contentDescription = null)
                }
            )
        }
    }
}
