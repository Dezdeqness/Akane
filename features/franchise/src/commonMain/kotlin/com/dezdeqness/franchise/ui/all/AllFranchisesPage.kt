package com.dezdeqness.franchise.ui.all

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.franchise.ui.composables.FranchiseCard
import com.dezdeqness.franchise.ui.composables.FranchiseError
import com.dezdeqness.franchise.ui.composables.FranchiseLoading
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllFranchisesPage(
    onBackPressed: () -> Unit,
    onFranchiseClicked: (franchiseId: String, franchiseName: String) -> Unit,
    viewModel: AllFranchisesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    Scaffold(
        topBar = {
            AppToolbar(
                title = {
                    Text(
                        text = "Франшизы",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigation = {
                    AppIconButton(
                        onClick = onBackPressed,
                        contentColor = AppTheme.colors.surface,
                    ) {
                        Icon(
                            imageVector = AkaneIcons.Back,
                            contentDescription = null,
                            tint = AppTheme.colors.textPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.background,
                    titleContentColor = AppTheme.colors.textPrimary,
                ),
            )
        },
        containerColor = AppTheme.colors.background,
    ) { padding ->
        AdaptiveLayout {
            val type = LocalLayoutType.current

            val columns = when (type) {
                LayoutType.Mobile -> 3
                LayoutType.Tablet -> 5
                LayoutType.Desktop -> 7
            }
            val horizontalPadding = when (type) {
                LayoutType.Mobile -> 16.dp
                LayoutType.Tablet -> 20.dp
                LayoutType.Desktop -> 24.dp
            }

            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                when (state.status) {
                    AllFranchisesStatus.Loading -> {
                        FranchiseLoading(
                            modifier = Modifier.fillMaxSize(),
                            columns = columns,
                            horizontalPadding = horizontalPadding,
                        )
                    }

                    AllFranchisesStatus.Error -> {
                        FranchiseError(
                            modifier = Modifier.align(Alignment.Center),
                            onAction = viewModel::onRetryClicked,
                        )
                    }

                    AllFranchisesStatus.Loaded -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = horizontalPadding,
                                vertical = 16.dp,
                            ),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(
                                count = state.franchises.size,
                                key = { index -> state.franchises[index].id },
                            ) { index ->
                                val franchise = state.franchises[index]
                                FranchiseCard(
                                    item = franchise,
                                    onClick = { onFranchiseClicked(franchise.id, franchise.name) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
