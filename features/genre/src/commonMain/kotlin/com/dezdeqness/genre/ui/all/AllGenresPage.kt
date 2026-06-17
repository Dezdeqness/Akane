package com.dezdeqness.genre.ui.all

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
import com.dezdeqness.genre.ui.composables.GenreCard
import com.dezdeqness.genre.ui.composables.GenreError
import com.dezdeqness.genre.ui.composables.GenreLoading
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllGenresPage(
    onBackPressed: () -> Unit,
    onGenreClicked: (genreId: Int, genreName: String) -> Unit,
    viewModel: AllGenresViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateOnLifecycle()

    Scaffold(
        topBar = {
            AppToolbar(
                title = {
                    Text(
                        text = "Жанры",
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
            val cardMinWidth = if (isMobile) 116.dp else 176.dp
            val horizontalPadding = if (isMobile) 16.dp else 28.dp

            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                when (state.status) {
                    AllGenresStatus.Loading -> {
                        GenreLoading(
                            modifier = Modifier.fillMaxSize(),
                            columns = if (isMobile) 3 else 5,
                            horizontalPadding = horizontalPadding,
                        )
                    }

                    AllGenresStatus.Error -> {
                        GenreError(
                            modifier = Modifier.align(Alignment.Center),
                            onAction = viewModel::onRetryClicked,
                        )
                    }

                    AllGenresStatus.Loaded -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(cardMinWidth),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = horizontalPadding,
                                vertical = 16.dp,
                            ),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(
                                count = state.genres.size,
                                key = { index -> state.genres[index].id },
                            ) { index ->
                                val genre = state.genres[index]
                                GenreCard(
                                    item = genre,
                                    onClick = { onGenreClicked(genre.id, genre.name) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
