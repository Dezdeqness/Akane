package com.dezdeqness.downloads.ui.activedownloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.downloads.contract.model.DownloadStatus
import com.dezdeqness.downloads.ui.model.DownloadUiModel

@Composable
internal fun DownloadItem(
    download: DownloadUiModel,
    onPauseClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = statusColor(download.status).copy(alpha = 0.15f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = statusIcon(download.status),
                contentDescription = null,
                tint = statusColor(download.status),
                modifier = Modifier.size(20.dp),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = "Эпизод ${download.episodeOrdinal} — ${download.episodeName}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = statusSubtitle(download),
                fontSize = 12.sp,
                color = AppTheme.colors.textSecondary,
            )
            if (download.status == DownloadStatus.DOWNLOADING) {
                LinearProgressIndicator(
                    progress = { download.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    color = Color(0xFF5D866C),
                    trackColor = AppTheme.colors.surface,
                    strokeCap = StrokeCap.Round,
                )
            }
        }

        when (download.status) {
            DownloadStatus.COMPLETED -> {
                AppIconButton(onClick = onDeleteClicked, contentColor = AppTheme.colors.background) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                    )
                }
            }

            DownloadStatus.DOWNLOADING,
            DownloadStatus.QUEUED,
                -> {
                AppIconButton(onClick = onPauseClicked, contentColor = AppTheme.colors.background) {
                    Icon(
                        Icons.Default.Pause,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                    )
                }
                AppIconButton(onClick = onCancelClicked, contentColor = AppTheme.colors.background) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                    )
                }
            }

            DownloadStatus.FAILED,
            DownloadStatus.PAUSED,
            DownloadStatus.CANCELLED,
                -> {
                AppIconButton(onClick = onRetryClicked, contentColor = AppTheme.colors.background) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                    )
                }
                AppIconButton(onClick = onDeleteClicked, contentColor = AppTheme.colors.background) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = AppTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}

private fun statusSubtitle(download: DownloadUiModel): String {
    val quality = download.quality
    return when (download.status) {
        DownloadStatus.QUEUED -> "$quality • В очереди"
        DownloadStatus.DOWNLOADING -> "$quality • ${(download.progress * 100).toInt()}%"
        DownloadStatus.COMPLETED -> "$quality • Завершено"
        DownloadStatus.FAILED -> "$quality • Ошибка сети"
        DownloadStatus.PAUSED -> "$quality • Пауза"
        DownloadStatus.CANCELLED -> "$quality • Отменено"
    }
}

private fun statusIcon(status: DownloadStatus): ImageVector {
    return when (status) {
        DownloadStatus.DOWNLOADING
            -> Icons.Default.ArrowDownward

        DownloadStatus.QUEUED -> Icons.Default.Schedule
        DownloadStatus.PAUSED -> Icons.Default.Schedule

        DownloadStatus.COMPLETED -> Icons.Default.CheckCircle
        DownloadStatus.FAILED -> Icons.Default.Error
        DownloadStatus.CANCELLED -> Icons.Default.Cancel
    }
}

private fun statusColor(status: DownloadStatus): Color {
    return when (status) {
        DownloadStatus.DOWNLOADING,

        DownloadStatus.QUEUED -> Color(0xFFEAB308)
        DownloadStatus.PAUSED -> Color(0xFF94A3B8)

        DownloadStatus.COMPLETED -> Color(0xFF4CAF50)
        DownloadStatus.FAILED -> Color(0xFFF44336)
        DownloadStatus.CANCELLED -> Color(0xFF94A3B8)
    }
}
