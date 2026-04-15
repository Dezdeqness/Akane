package com.dezdeqness.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dezdeqness.home.ui.model.HomeUiModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomePageStandalone(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
    onItemClicked: (Long, String) -> Unit,
) {
    val actions = remember {
        object : HomeActions {
            override fun onItemClicked(details: HomeUiModel) {
                onItemClicked(details.id, details.name)
            }

            override fun onRetryClicked() {
                homeViewModel.onRetryClicked()
            }
        }
    }

    HomePage(
        modifier = modifier,
        stateFlow = homeViewModel.homeState,
        actions = actions,
    )
}
