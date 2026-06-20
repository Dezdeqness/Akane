package com.dezdeqness.home.ui.composables.franchise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.header.Header
import com.dezdeqness.home.ui.model.FranchisePanelUiModel

@Composable
fun FranchisesSection(
    modifier: Modifier = Modifier,
    franchises: List<FranchisePanelUiModel>,
    onFranchiseClicked: (FranchisePanelUiModel) -> Unit,
    onAllClicked: () -> Unit,
) {
    if (franchises.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Header(
                title = "Франшизы",
                titleStyle = AppTheme.typography.labelLarge.copy(fontSize = 18.sp),
                onClick = onAllClicked,
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(
                items = franchises,
                key = { it.id },
            ) { franchise ->
                FranchisePanelItem(
                    item = franchise,
                    cardWidth = 116.dp,
                    titleStyle = AppTheme.typography.bodyMedium,
                    onClick = { onFranchiseClicked(franchise) },
                )
            }
        }
    }
}
