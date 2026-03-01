package com.dezdeqness.videoplayer.core.player.composables.dropdown

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.videoplayer.core.player.feature.ui.PlaybackSpeed

@Composable
fun VideoSpeedDropdownMenu(
    isExpanded: Boolean,
    currentSpeed: PlaybackSpeed,
    onSpeedChange: (PlaybackSpeed) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { onDismiss() }
    ) {
        PlaybackSpeed.entries.forEach { item ->
            DropdownMenuItem(
                text = { Text(item.label, color = Color.Black) },
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
