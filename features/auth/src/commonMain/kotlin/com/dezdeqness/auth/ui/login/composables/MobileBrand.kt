package com.dezdeqness.auth.ui.login.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme

@Composable
fun MobileBrand() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(AppTheme.colors.primary)
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(
                text = "Akane",
                color = AppTheme.colors.onPrimary,
                style = AppTheme.typography.headlineSmall,
            )
        }
        Text(
            text = "Тысячи тайтлов на любой вкус",
            color = AppTheme.colors.textSecondary,
            style = AppTheme.typography.bodyMedium,
        )
    }
}
