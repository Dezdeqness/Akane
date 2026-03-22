package com.dezdeqness.details.ui.composables.episodes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EpisodeQualitySelectionDialog(
    availableQualities: Map<String, String>,
    onQualitySelected: (quality: String, url: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedQuality by remember { mutableStateOf(availableQualities.keys.lastOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите качество") },
        text = {
            Column {
                availableQualities.forEach { (quality, _) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedQuality = quality }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedQuality == quality,
                            onClick = { selectedQuality = quality },
                        )
                        Text(
                            text = quality,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val quality = selectedQuality ?: return@TextButton
                    val url = availableQualities[quality] ?: return@TextButton
                    onQualitySelected(quality, url)
                },
            ) {
                Text("Скачать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}

@Composable
fun QualitySelectionDialogBatch(
    availableQualities: List<String>,
    onQualitySelected: (quality: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedQuality by remember { mutableStateOf(availableQualities.lastOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите качество для всех эпизодов") },
        text = {
            Column {
                availableQualities.forEach { quality ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedQuality = quality }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedQuality == quality,
                            onClick = { selectedQuality = quality },
                        )
                        Text(
                            text = quality,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val quality = selectedQuality ?: return@TextButton
                    onQualitySelected(quality)
                },
            ) {
                Text("Скачать все")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}
