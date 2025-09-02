package com.dezdeqness.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomePageStandalone(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
    onItemClicked: (Long) -> Unit
) {
    val actions = remember {
        object : HomeActions {
            override fun onItemClicked(id: Long) {
                onItemClicked(id)
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
