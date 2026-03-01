package com.dezdeqness.personal.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.personal.ui.composables.PersonalEmpty
import com.dezdeqness.personal.ui.composables.PersonalGrid
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
            if (state.list.isEmpty()) {
                PersonalEmpty(
                    modifier = Modifier.align(Alignment.Center),
                    onAction = actions::onEmptyListActionClicked,
                )
            } else {
                PersonalGrid(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    items = state.list,
                    onItemClicked = actions::onItemClicked,
                )
            }
        }
    }
}
