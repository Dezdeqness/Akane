package com.dezdeqness.details.ui.composables.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.details.ui.model.DetailsTab

@Composable
fun StatisticsTabContent(statistics: DetailsTab.StatisticsTab) {
    val items = listOf(
        "В избранном" to statistics.userFavourites,
        "Запланировано" to statistics.planned,
        "Просмотрено" to statistics.watched,
        "Смотрят" to statistics.watching,
        "Отложено" to statistics.postponed,
        "Брошено" to statistics.abandoned
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Header(
            title = "Статистика пользователей",
            titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                items.forEachIndexed { index, (label, value) ->
                    StatisticRow(label, value)
                    if (index != items.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}
