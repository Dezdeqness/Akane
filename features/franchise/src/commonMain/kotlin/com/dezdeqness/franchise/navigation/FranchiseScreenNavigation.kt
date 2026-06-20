package com.dezdeqness.franchise.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.franchise.ui.all.AllFranchisesPage
import com.dezdeqness.franchise.ui.detail.FranchiseDetailPage
import com.dezdeqness.franchise.ui.detail.FranchiseDetailViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object FranchisesRoute : NavKey

@Serializable
data class FranchiseDetailRoute(
    val franchiseId: String,
    val franchiseName: String,
) : NavKey

fun EntryProviderScope<NavKey>.franchiseEntries(
    onBackPressed: () -> Unit,
    onFranchiseClicked: (franchiseId: String, franchiseName: String) -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
) {
    entry<FranchisesRoute> {
        AllFranchisesPage(
            onBackPressed = onBackPressed,
            onFranchiseClicked = onFranchiseClicked,
        )
    }
    entry<FranchiseDetailRoute> { key ->
        val viewModel: FranchiseDetailViewModel = koinViewModel { parametersOf(key.franchiseId) }
        FranchiseDetailPage(
            franchiseName = key.franchiseName,
            onBackPressed = onBackPressed,
            onReleaseClicked = onReleaseClicked,
            viewModel = viewModel,
        )
    }
}

fun NavBackStack<NavKey>.navigateToFranchises() {
    add(FranchisesRoute)
}

fun NavBackStack<NavKey>.navigateToFranchiseDetail(franchiseId: String, franchiseName: String) {
    add(FranchiseDetailRoute(franchiseId = franchiseId, franchiseName = franchiseName))
}
