package com.dezdeqness.downloads.ui.activedownloads

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.downloads.ui.model.DownloadUiModel

@Composable
internal fun DownloadsList(
    activeDownloads: List<DownloadUiModel>,
    historyDownloads: List<DownloadUiModel>,
    completedDownloads: List<DownloadUiModel>,
    onDeleteClicked: (Long) -> Unit,
    onRetryClicked: (Long) -> Unit,
    onCancelClicked: (Long) -> Unit,
    onPauseClicked: (Long) -> Unit,
    onRemoveFromHistory: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        if (activeDownloads.isNotEmpty()) {
            item {
                SectionHeader("Активные")
            }
            items(
                items = activeDownloads,
                key = { it.id },
            ) { download ->
                DownloadItem(
                    download = download,
                    onDeleteClicked = { onDeleteClicked(download.id) },
                    onRetryClicked = { onRetryClicked(download.id) },
                    onCancelClicked = { onCancelClicked(download.id) },
                    onPauseClicked = { onPauseClicked(download.id) },
                )
            }
        }
        if (historyDownloads.isNotEmpty()) {
            if (activeDownloads.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item {
                SectionHeader("Ошибки")
            }
            items(
                items = historyDownloads,
                key = { it.id },
            ) { download ->
                DownloadItem(
                    download = download,
                    onDeleteClicked = { onDeleteClicked(download.id) },
                    onRetryClicked = { onRetryClicked(download.id) },
                    onCancelClicked = { onCancelClicked(download.id) },
                    onPauseClicked = { onPauseClicked(download.id) },
                )
            }
        }
        if (completedDownloads.isNotEmpty()) {
            if (activeDownloads.isNotEmpty() || historyDownloads.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item {
                SectionHeader("История")
            }
            items(
                items = completedDownloads,
                key = { "completed_${it.id}" },
            ) { download ->
                DownloadItem(
                    download = download,
                    onDeleteClicked = { onRemoveFromHistory(download.id) },
                    onRetryClicked = { onRetryClicked(download.id) },
                    onCancelClicked = { onCancelClicked(download.id) },
                    onPauseClicked = { onPauseClicked(download.id) },
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppTheme.colors.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 4.dp),
    )
}
