package com.dezdeqness.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dezdeqness.home.ui.model.FranchisePanelUiModel
import com.dezdeqness.home.ui.model.GenrePanelUiModel
import com.dezdeqness.home.ui.model.HomeUiModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomePageStandalone(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
    onItemClicked: (Long, String) -> Unit,
    onGenreClicked: (Int, String) -> Unit,
    onAllGenresClicked: () -> Unit,
    onFranchiseClicked: (String, String) -> Unit,
    onAllFranchisesClicked: () -> Unit,
) {
    val actions = remember {
        object : HomeActions {
            override fun onItemClicked(details: HomeUiModel) {
                onItemClicked(details.id, details.name)
            }

            override fun onGenreClicked(genre: GenrePanelUiModel) {
                onGenreClicked(genre.id, genre.name)
            }

            override fun onAllGenresClicked() {
                onAllGenresClicked()
            }

            override fun onPromoReleaseClicked(releaseId: Long, title: String) {
                onItemClicked(releaseId, title)
            }

            override fun onFranchiseClicked(franchise: FranchisePanelUiModel) {
                onFranchiseClicked(franchise.id, franchise.name)
            }

            override fun onAllFranchisesClicked() {
                onAllFranchisesClicked()
            }

            override fun onRetryClicked() {
                homeViewModel.retry()
            }
        }
    }

    HomePage(
        modifier = modifier,
        stateFlow = homeViewModel.homeState,
        actions = actions,
    )
}
