package com.dezdeqness.personal.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PersonalStandalonePage(
    modifier: Modifier = Modifier,
    personalViewModel: PersonalViewModel = koinViewModel(),
    onItemClicked: (Long) -> Unit,
    onEmptyListActionClicked: () -> Unit,
) {
    val actions = remember {
        object : PersonalActions {
            override fun onItemClicked(id: Long) {
                onItemClicked(id)
            }

            override fun onItemUnFavouriteClicked(id: Long) {
                personalViewModel.onItemUnFavouriteClicked(id)
            }

            override fun onEmptyListActionClicked() {
                onEmptyListActionClicked()
            }

        }
    }

    PersonalPage(
        modifier = modifier,
        stateFlow = personalViewModel.personalStateFlow,
        actions = actions,
    )
}
