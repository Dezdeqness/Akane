package com.dezdeqness.details.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dezdeqness.details.ui.DetailsPage

const val RELEASE_ID = "releaseId"
const val DETAILS_ROUTE = "details_route/{$RELEASE_ID}"

fun NavGraphBuilder.detailsScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        DETAILS_ROUTE,
        arguments = listOf(
            navArgument(RELEASE_ID) { type = NavType.LongType },
        ),
    ) {
        DetailsPage(onBackPressed = onBackPressed)
    }
}

fun NavHostController.navigateToDetailsScreen(releaseId: Long) {
    navigate("details_route/$releaseId")
}
