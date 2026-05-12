package com.dezdeqness.auth.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme

@Composable
fun AuthBrandPanel(
    modifier: Modifier = Modifier,
    showStats: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        AppTheme.colors.primary,
                        AppTheme.colors.primaryVariant,
                    ),
                ),
            )
            .padding(48.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppTheme.colors.onPrimary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "A",
                    color = AppTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                )
            }

            Text(
                text = "Akane",
                color = AppTheme.colors.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
            )

            Text(
                text = "Тысячи тайтлов на любой вкус",
                color = AppTheme.colors.onPrimary.copy(alpha = 0.85f),
                style = AppTheme.typography.bodyMedium,
            )

            if (showStats) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Stat("10K+", "релизов")
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .padding(vertical = 4.dp)
                            .background(AppTheme.colors.onPrimary.copy(alpha = 0.4f)),
                    )
                    Stat("HD", "качество")
                }
            }
        }
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = AppTheme.colors.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
        )
        Text(
            text = label,
            color = AppTheme.colors.onPrimary.copy(alpha = 0.85f),
            style = AppTheme.typography.bodySmall,
        )
    }
}
