package com.dezdeqness.videoplayer.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
                    if (item == currentSpeed) Icon(Icons.Default.Check, contentDescription = null)
                }
            )
        }
    }
}
