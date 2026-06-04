package com.dezdeqness.personal.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.personal.ui.composables.PersonalEmpty
import com.dezdeqness.personal.ui.composables.PersonalError
import com.dezdeqness.personal.ui.composables.PersonalGrid
import com.dezdeqness.personal.ui.composables.PersonalUnauthorized
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalPage(
    modifier: Modifier = Modifier,
    stateFlow: StateFlow<PersonalState>,
    actions: PersonalActions,
) {
    val state by stateFlow.collectAsStateOnLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = AppTheme.colors.background,
        topBar = {
            AppToolbar(
                title = "Сохраненные",
                navigationIcon = null,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.colors.background),
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (state.status) {
                Status.Initial, Status.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppTheme.colors.primary,
                    )
                }

                Status.Error -> {
                    PersonalError(
                        modifier = Modifier.align(Alignment.Center),
                        onAction = actions::onRetryClicked,
                    )
                }

                Status.Unauthorized -> {
                    PersonalUnauthorized(
                        modifier = Modifier.align(Alignment.Center),
                        onAction = actions::onNavigateToProfile,
                    )
                }

                Status.Empty -> {
                    PersonalEmpty(
                        modifier = Modifier.align(Alignment.Center),
                        onAction = actions::onEmptyListActionClicked,
                    )
                }

                Status.Loaded -> {
                    AdaptiveLayout(modifier = Modifier.fillMaxSize()) {
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

                        var isPageLoading by remember { mutableStateOf(false) }

                        LaunchedEffect(state.list) {
                            isPageLoading = false
                        }

                        PersonalGrid(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = horizontalPadding),
                            columns = GridCells.Fixed(columns),
                            items = state.list,
                            hasNextPage = state.hasNextPage,
                            isPageLoading = isPageLoading,
                            onLoadMore = {
                                actions.onLoadMore()
                                isPageLoading = true
                            },
                            onItemClicked = actions::onItemClicked,
                        )
                    }
                }
            }
        }
    }
}
