package com.dezdeqness.genre.ui.releases

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dezdeqness.catalog.ui.ReleaseGrid
import com.dezdeqness.catalog.ui.ReleaseListLoading
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.buttons.AppIconButton
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.icons.AkaneIcons
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.genre.ui.composables.GenreError
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreReleasesPage(
    genreName: String,
    onBackPressed: () -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
    viewModel: GenreReleasesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    Scaffold(
        topBar = {
            AppToolbar(
                title = {
                    Text(
                        text = genreName,
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
            val isMobile = LocalLayoutType.current == LayoutType.Mobile
            val columns = if (isMobile) 3 else 6
            val horizontalPadding = if (isMobile) 8.dp else 20.dp

            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                when (state.status) {
                    GenreReleasesStatus.Loading -> {
                        ReleaseListLoading(
                            modifier = Modifier.fillMaxSize(),
                            gridElementCount = columns,
                        )
                    }

                    GenreReleasesStatus.Error -> {
                        GenreError(
                            modifier = Modifier.align(Alignment.Center),
                            onAction = viewModel::onRetryClicked,
                        )
                    }

                    GenreReleasesStatus.Empty -> {
                        Text(
                            text = "В этом жанре пока нет релизов",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textSecondary,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    GenreReleasesStatus.Loaded -> {
                        var isPageLoading by remember { mutableStateOf(false) }

                        LaunchedEffect(state.items) {
                            isPageLoading = false
                        }

                        ReleaseGrid(
                            list = state.items,
                            hasNextPage = state.hasNextPage,
                            isPageLoading = isPageLoading,
                            columnCount = columns,
                            contentPadding = PaddingValues(
                                horizontal = horizontalPadding,
                                vertical = 8.dp,
                            ),
                            onLoadMore = {
                                viewModel.onLoadMore()
                                isPageLoading = true
                            },
                            onReleaseClicked = onReleaseClicked,
                        )
                    }
                }
            }
        }
    }
}
