package com.dezdeqness.videoplayer.core.player.composables.dropdown

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.videoplayer.core.player.data.MediaQuality
import com.dezdeqness.videoplayer.core.player.data.QualityVariant

@Composable
fun QualityDropdownMenu(
    isExpanded: Boolean,
    variants: List<QualityVariant>,
    currentQuality: MediaQuality?,
    onQualityChange: (MediaQuality) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { onDismiss() },
        containerColor = Color.Black,
    ) {
        variants.forEach { variant ->
            DropdownMenuItem(
                text = { Text(variant.quality.nameQuality) },
                onClick = {
                    onQualityChange(variant.quality)
                    onDismiss()
                },
                trailingIcon = {
                    if (variant.quality == currentQuality) {
                        Icon(AkaneIcons.Check, contentDescription = null)
                    }
                },
                colors = MenuDefaults.itemColors().copy(
                    trailingIconColor = Color.White,
                    textColor = Color.White,
                ),
            )
        }
    }
}
