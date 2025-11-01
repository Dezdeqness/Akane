package com.dezdeqness.details.ui.composables.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme

@Composable
fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            fontSize = 14.sp,
            color = AppTheme.colors.textPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
