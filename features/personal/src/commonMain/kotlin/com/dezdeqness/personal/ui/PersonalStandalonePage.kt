package com.dezdeqness.personal.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dezdeqness.personal.ui.model.PersonalUiModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PersonalStandalonePage(
    modifier: Modifier = Modifier,
    personalViewModel: PersonalViewModel = koinViewModel(),
    onItemClicked: (Long, String) -> Unit,
    onEmptyListActionClicked: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val actions = remember {
        object : PersonalActions {
            override fun onItemClicked(details: PersonalUiModel) {
                onItemClicked(details.id, details.name)
            }

            override fun onEmptyListActionClicked() {
                onEmptyListActionClicked()
            }

            override fun onLoadMore() {
                personalViewModel.onLoadMore()
            }

            override fun onRetryClicked() {
                personalViewModel.onRetryClicked()
            }

            override fun onNavigateToProfile() {
                onNavigateToProfile()
            }

        }
    }

    PersonalPage(
        modifier = modifier,
        stateFlow = personalViewModel.personalStateFlow,
        actions = actions,
    )
}
