package com.dezdeqness.details.ui.composables.franchise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.details.ui.composables.core.MetadataRow
import com.dezdeqness.details.ui.model.DetailsTab

@Composable
fun FranchiseTabContent(
    franchise: DetailsTab.FranchiseTab,
    onReleaseClicked: (Long, String) -> Unit,
) {
    val items = listOf(
        "Общая длительность" to franchise.totalDuration,
        "Всего релизов" to franchise.totalReleases.toString(),
        "Всего эпизодов" to franchise.totalEpisodes.toString(),
        "Годы выхода" to "${franchise.firstYear} - ${franchise.lastYear}"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = franchise.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (franchise.nameEnglish.isNotEmpty()) {
                    Text(
                        text = franchise.nameEnglish,
                        fontSize = 14.sp,
                        color = AppTheme.colors.textSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                items.forEachIndexed { index, (label, value) ->
                    MetadataRow(label, value)
                    if (index != items.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        Header(
            title = "Релизы",
            titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
        )

        AdaptiveLayout {
            val type = LocalLayoutType.current

            when (type) {
                LayoutType.Mobile -> {
                    Column {
                        franchise.releases.forEach { release ->
                            FranchiseReleaseItemMobile(
                                release = release,
                                onReleaseClicked = onReleaseClicked,
                            )
                        }
                    }
                }

                LayoutType.Tablet,
                LayoutType.Desktop,
                    -> {
                    FranchiseReleasesGridWide(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        franchise = franchise,
                        onReleaseClicked = onReleaseClicked,
                    )
                }
            }
        }
    }
}
