package com.dezdeqness.feed.ui.filter.composables.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.feed.ui.filter.ChipFilterSectionUiModel

@Composable
fun GenresMultiSelectDialog(
    section: ChipFilterSectionUiModel,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit,
) {
    var localSelection by remember {
        mutableStateOf(section.selectedCells)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(section.displayName) },
        confirmButton = {
            TextButton(onClick = { onSave(localSelection) }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(section.items.size) { index ->
                    val item = section.items[index]
                    val checked = item.id in localSelection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localSelection = if (checked) {
                                    localSelection - item.id
                                } else {
                                    localSelection + item.id
                                }
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(item.title)
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                localSelection = if (isChecked) {
                                    localSelection + item.id
                                } else {
                                    localSelection - item.id
                                }
                            },
                        )
                    }
                }
            }
        }
    )
}
