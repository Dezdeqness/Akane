package com.dezdeqness.videoplayer.ui.composables.dropdown

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.videoplayer.ui.VideoSpeed

@Composable
fun VideoSpeedDropdownMenu(
    isExpanded: Boolean,
    currentSpeed: VideoSpeed,
    onSpeedChange: (VideoSpeed) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { onDismiss() }
    ) {
        VideoSpeed.entries.toTypedArray().forEach { item ->
            DropdownMenuItem(
                text = { Text("${item.speed}x", color = Color.Black) },
                onClick = {
                    onSpeedChange(item)
                    onDismiss()
                },
                trailingIcon = {
                    if (item == currentSpeed) Icon(AkaneIcons.Check, contentDescription = null)
                }
            )
        }
    }
}
