package com.dezdeqness.personal.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dezdeqness.personal.ui.composables.PersonalCell
import com.dezdeqness.personal.ui.composables.PersonalEmptyState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalPage(
    modifier: Modifier = Modifier,
    stateFlow: StateFlow<PersonalState>,
    actions: PersonalActions,
) {
    val state by stateFlow.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Сохраненные")
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (state.list.isEmpty()) {
                PersonalEmptyState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        count = state.list.size,
                        key = { index ->
                            state.list[index].id
                        },
                    ) { index ->
                        val item = state.list[index]

                        PersonalCell(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .padding(vertical = 4.dp, horizontal = 16.dp)
                                .clickable(
                                    onClick = {
                                        actions.onItemClicked(item.id)
                                    }
                                ),
                            item = item,
                            onRemoveItemClicked = actions::onItemUnFavouriteClicked,
                        )
                    }
                }
            }
        }
    }
}
